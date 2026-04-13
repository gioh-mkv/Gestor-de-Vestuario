package service;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import model.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Serviço responsável pela persistência dos dados em JSON.
 *
 * Correções aplicadas em relação à versão original:
 * - Serialização polimórfica de Item via campo "__type" preserva o tipo
 *   concreto (Camisa, Calca, Relogio, RoupaIntima) ao recarregar.
 * - Persistência de Lavagens adicionada (estava ausente no original).
 * - Arquivos de dados salvos na pasta "dados/" ao lado do JAR.
 */
public class PersistenciaService {

    private static final String DIR_DADOS    = obterDiretorioDados();
    private static final String ARQUIVO_ITENS    = DIR_DADOS + "itens.json";
    private static final String ARQUIVO_LOOKS    = DIR_DADOS + "looks.json";
    private static final String ARQUIVO_LAVAGENS = DIR_DADOS + "lavagens.json";

    private final Gson gson;

    public PersistenciaService() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeHierarchyAdapter(Item.class, new ItemPolymorphicAdapter())
                .setPrettyPrinting()
                .create();
        criarDiretorioDados();
    }

    // -- Itens ----------------------------------------------------------------

    public void salvarItens(List<Item> itens) { salvarJson(ARQUIVO_ITENS, itens); }

    public List<Item> carregarItens() {
        String json = lerArquivo(ARQUIVO_ITENS);
        if (json == null) return new ArrayList<>();
        try {
            Type t = new TypeToken<List<Item>>(){}.getType();
            List<Item> lista = gson.fromJson(json, t);
            return lista != null ? lista : new ArrayList<>();
        } catch (JsonParseException e) {
            System.err.println("[GVP] Erro ao carregar itens: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // -- Looks ----------------------------------------------------------------

    public void salvarLooks(List<Look> looks) { salvarJson(ARQUIVO_LOOKS, looks); }

    public List<Look> carregarLooks() {
        String json = lerArquivo(ARQUIVO_LOOKS);
        if (json == null) return new ArrayList<>();
        try {
            Type t = new TypeToken<List<Look>>(){}.getType();
            List<Look> lista = gson.fromJson(json, t);
            return lista != null ? lista : new ArrayList<>();
        } catch (JsonParseException e) {
            System.err.println("[GVP] Erro ao carregar looks: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // -- Lavagens (NOVO) ------------------------------------------------------

    public void salvarLavagens(List<LavagemDto> lavagens) { salvarJson(ARQUIVO_LAVAGENS, lavagens); }

    public List<LavagemDto> carregarLavagens() {
        String json = lerArquivo(ARQUIVO_LAVAGENS);
        if (json == null) return new ArrayList<>();
        try {
            Type t = new TypeToken<List<LavagemDto>>(){}.getType();
            List<LavagemDto> lista = gson.fromJson(json, t);
            return lista != null ? lista : new ArrayList<>();
        } catch (JsonParseException e) {
            System.err.println("[GVP] Erro ao carregar lavagens: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /** DTO leve para Lavagem — armazena apenas IDs dos itens lavados. */
    public static class LavagemDto {
        public String id;
        public LocalDate data;
        public List<String> idsItens = new ArrayList<>();
        public String observacoes;
    }

    // -- Helpers --------------------------------------------------------------

    private void salvarJson(String caminho, Object obj) {
        try (Writer w = new OutputStreamWriter(
                new FileOutputStream(caminho), StandardCharsets.UTF_8)) {
            gson.toJson(obj, w);
        } catch (IOException e) {
            System.err.println("[GVP] Erro ao salvar " + caminho + ": " + e.getMessage());
        }
    }

    private String lerArquivo(String caminho) {
        File f = new File(caminho);
        if (!f.exists()) return null;
        try {
            return new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("[GVP] Erro ao ler " + caminho + ": " + e.getMessage());
            return null;
        }
    }

    private void criarDiretorioDados() {
        File dir = new File(DIR_DADOS);
        if (!dir.exists()) dir.mkdirs();
    }

    private static String obterDiretorioDados() {
        try {
            String p = PersistenciaService.class
                    .getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            return new File(p).getParentFile().getAbsolutePath()
                    + File.separator + "dados" + File.separator;
        } catch (Exception e) {
            return "dados" + File.separator;
        }
    }

    // -- Gson Adapters --------------------------------------------------------

    private static class LocalDateAdapter
            implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE;
        public JsonElement serialize(LocalDate src, Type t, JsonSerializationContext c) {
            return new JsonPrimitive(FMT.format(src));
        }
        public LocalDate deserialize(JsonElement j, Type t, JsonDeserializationContext c) {
            return LocalDate.parse(j.getAsString(), FMT);
        }
    }

    /**
     * Adapter polimórfico para Item.
     * Adiciona campo "__type" na serialização e usa-o para instanciar
     * a classe concreta correta na desserialização.
     */
    private static class ItemPolymorphicAdapter
            implements JsonSerializer<Item>, JsonDeserializer<Item> {

        private static final String TIPO = "__type";

        private Gson plainGson() {
            return new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .create();
        }

        public JsonElement serialize(Item src, Type type, JsonSerializationContext ctx) {
            JsonObject obj = plainGson().toJsonTree(src).getAsJsonObject();
            obj.addProperty(TIPO, src.getClass().getSimpleName());
            return obj;
        }

        public Item deserialize(JsonElement json, Type type, JsonDeserializationContext ctx)
                throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            if (!obj.has(TIPO))
                throw new JsonParseException("Campo '__type' ausente nos dados salvos.");
            String nome = obj.get(TIPO).getAsString();
            Gson g = plainGson();
            switch (nome) {
                case "Camisa":      return g.fromJson(obj, Camisa.class);
                case "Calca":       return g.fromJson(obj, Calca.class);
                case "Relogio":     return g.fromJson(obj, Relogio.class);
                case "RoupaIntima": return g.fromJson(obj, RoupaIntima.class);
                default: throw new JsonParseException("Tipo desconhecido: " + nome);
            }
        }
    }
}
