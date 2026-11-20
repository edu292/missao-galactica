/*
 * Projeto Missão Galáctica, Cenário A (Estação Espacial Órbita-7): registra os tripulantes da estação,
 * ordena por código galáctico com Bubble Sort, Insertion Sort e Quick Sort e compara o desempenho dos três.
 */
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

/* Um tripulante da estação. O código galáctico é o campo usado para ordenar. */
class Tripulante {
    int codigo;
    String nome;
    String planetaNatal;
    String funcao;

    // monta o tripulante com os quatro dados (os mesmos do arquivo)
    public Tripulante(int codigo, String nome, String planetaNatal, String funcao) {
        this.codigo = codigo;
        this.nome = nome;
        this.planetaNatal = planetaNatal;
        this.funcao = funcao;
    }

    /* Monta a linha que aparece na listagem, com as colunas alinhadas. */
    @Override
    public String toString() {
        return String.format("Código: %d | Nome: %-22s | Planeta: %-15s | Função: %s",
                codigo, nome, planetaNatal, funcao);
    }
}

/* Guarda o resultado de uma ordenação: quantas comparações, quantas trocas e quanto tempo levou. */
class EstatisticasOrdenacao {
    long comparacoes;
    long trocas;
    // tempo em nanossegundos, medido com System.nanoTime()
    long tempoNano;
}

/* Classe principal: tem o menu, a leitura dos dados, os três algoritmos e a Grande Corrida. */
public class MissaoGalactica {
    /* Carrega os tripulantes do arquivo e fica mostrando o menu até a pessoa escolher 0. */
    public static void main(String[] args) {
        List<Tripulante> tripulantes = carregarTripulantes("tripulantes.txt");
        Scanner scanner = new Scanner(System.in);

        int opcao;
        do {
            System.out.println();
            System.out.println("╔═══════════════════════════════════════════╗");
            System.out.println("║            ★ MISSÃO GALÁCTICA ★           ║");
            System.out.println("╠═══════════════════════════════════════════╣");
            System.out.println("║ 1 - Registrar tripulante                  ║");
            System.out.println("║ 2 - Listar tripulantes                    ║");
            System.out.println("║ 3 - Organizar embarque na cápsula         ║");
            System.out.println("║ 4 - Modo passo a passo                    ║");
            System.out.println("║ 5 - A Grande Corrida                      ║");
            System.out.println("║ 0 - Encerrar a missão                     ║");
            System.out.println("╚═══════════════════════════════════════════╝");
            System.out.print("Escolha uma opção: ");

            // se a pessoa digitar letra, descarta e pede de novo
            while (!scanner.hasNextInt()) {
                System.out.print("Por favor, digite um número válido: ");
                scanner.next();
            }

            opcao = scanner.nextInt();
            // limpa o Enter que sobra depois do nextInt
            scanner.nextLine();
            switch (opcao) {
                case 1 -> registrarTripulante(tripulantes, scanner);
                case 2 -> listarTripulantes(tripulantes);
                case 3 -> organizarEmbarque(tripulantes, scanner);
                case 4 -> modoPassoAPasso(tripulantes, scanner);
                case 5 -> executarGrandeCorrida();
                case 0 -> System.out.println("\nEncerrando a missão...");
                default -> System.out.println("\nOpção inválida! Tente novamente.");
            }
        } while (opcao != 0);

        scanner.close();
    }

    /*
     * Cria tripulantes aleatórios para a Grande Corrida, misturando nomes, sobrenomes, planetas e funções.
     * Devolve a lista com códigos de 8 dígitos sem repetir.
     */
    public static List<Tripulante> gerarTripulantes(int quantidade) {
        String[] NOMES = {
                "Bip", "Pipoca", "Cosmo", "Dona", "Zeca", "Bolinha", "Chico", "Lelé", "Paçoca", "Tutu",
                "Nino", "Fifi", "Biscoito", "Pingo", "Zezé", "Babu", "Tatá", "Kika", "Pudim", "Bento"
        };
        String[] SOBRENOMES = {
                "Supernova", "Quântica", "Estelar", "Cósmico", "Nebuloso", "Sideral", "Gravidade",
                "Cometa", "Asteroide", "Espectral", "Luz-Ano", "Vácuo", "Propulsor", "Órbita", "Laser"
        };
        String[] PLANETAS = {
                "Marte", "Júpiter", "Plutão", "Vênus", "Mercúrio", "Vega", "Netuno",
                "Saturno", "Kepler", "Órion", "Urano", "Sírius", "Andrômeda", "Alfa Centauri"
        };
        String[] FUNCOES = {
                "Piloto", "Engenheiro de dobra", "Navegador", "Cozinheiro espacial", "Astrônomo",
                "Técnico de antenas", "Médico de bordo", "Mecânico", "Cartógrafo", "Observador",
                "Botânico", "Cientista", "Comunicações", "Faxineiro estelar", "Músico de bordo"
        };

        // seed fixa 1990: os números aleatórios saem iguais em toda execução,
        // então as comparações e trocas da Grande Corrida dão sempre o mesmo resultado
        Random random = new Random(1990);
        // guarda os códigos já sorteados para não repetir
        Set<Integer> codigosUsados = new HashSet<>();
        List<Tripulante> tripulantes = new ArrayList<>(quantidade);

        for (int posicao = 0; posicao < quantidade; posicao++) {
            int codigo;
            // sorteia um código de 8 dígitos (de 10000000 a 99999999).
            // o add do Set devolve false se o código já existe, aí sorteia outro
            do {
                codigo = 10_000_000 + random.nextInt(90_000_000);
            } while (!codigosUsados.add(codigo));

            String nome = NOMES[random.nextInt(NOMES.length)] + " " + SOBRENOMES[random.nextInt(SOBRENOMES.length)];
            String planeta = PLANETAS[random.nextInt(PLANETAS.length)];
            String funcao = FUNCOES[random.nextInt(FUNCOES.length)];
            tripulantes.add(new Tripulante(codigo, nome, planeta, funcao));
        }

        return tripulantes;
    }

    /*
     * Lê o tripulantes.txt (uma linha por tripulante, no formato codigo;nome;planeta;funcao)
     * e devolve a lista. Se o arquivo não existir, o programa fecha.
     */
    public static List<Tripulante> carregarTripulantes(String caminhoArquivo) {
        List<Tripulante> tripulantes = new ArrayList<>();
        Path path = Path.of(caminhoArquivo);
        // sem o arquivo não tem a tripulação inicial, então encerra
        if (!Files.exists(path)) {
            System.out.println("Erro: Arquivo '" + caminhoArquivo + "' não encontrado. Encerrando...");
            System.exit(1);
        }

        try

        {
            for (String linha : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                if (linha.isBlank()) {
                    continue;
                }
                // separa os campos pelo ; e tira os espaços das pontas
                String[] partes = linha.split(";");
                int codigo = Integer.parseInt(partes[0].trim());
                String nome = partes[1].trim();
                String planeta = partes[2].trim();
                String funcao = partes[3].trim();

                tripulantes.add(new Tripulante(codigo, nome, planeta, funcao));
            }
        } catch (IOException e) {
            System.out.println("Aviso: Não foi possível ler o arquivo " + caminhoArquivo + ". Iniciando lista vazia.");
        }

        return tripulantes;
    }

    /*
     * Pede os dados de um tripulante novo e adiciona na lista.
     * Só aceita código com 8 dígitos e que ninguém esteja usando.
     */
    static void registrarTripulante(List<Tripulante> tripulantes, Scanner scanner) {
        System.out.println();
        System.out.println("--- REGISTRAR NOVO TRIPULANTE ---");
        System.out.print("Digite o código galáctico (8 dígitos): ");
        // insiste até a pessoa digitar um número
        while (!scanner.hasNextInt()) {
            System.out.print("Código inválido. Digite um número inteiro de 8 dígitos: ");
            scanner.next();
        }
        int codigo = scanner.nextInt();
        scanner.nextLine();

        // 8 dígitos = entre 10.000.000 e 99.999.999
        if (codigo < 10_000_000 || codigo > 99_999_999) {
            System.out.println("Erro: O código galáctico deve ter exatamente 8 dígitos!");
            return;
        }

        // código repetido não pode, porque é por ele que a gente ordena
        for (Tripulante t : tripulantes) {
            if (t.codigo == codigo) {
                System.out.println("Erro: Já existe um tripulante com esse código galáctico!");
                return;
            }
        }

        System.out.print("Digite o nome: ");
        String nome = scanner.nextLine();
        System.out.print("Digite o planeta natal: ");
        String planeta = scanner.nextLine();
        System.out.print("Digite a função a bordo: ");
        String funcao = scanner.nextLine();

        tripulantes.add(new Tripulante(codigo, nome, planeta, funcao));
        System.out.println();
        System.out.println("Tripulante registrado com sucesso!");
    }

    /* Mostra todos os tripulantes na ordem em que estão na lista. */
    static void listarTripulantes(List<Tripulante> tripulantes) {
        System.out.println();
        System.out.println("--- TRIPULAÇÃO ATUAL ---");
        for (Tripulante tripulante : tripulantes) {
            System.out.println(tripulante);
        }
    }

    /*
     * Ordena os tripulantes com o algoritmo escolhido e mostra a fila da cápsula,
     * com comparações, trocas e tempo. A lista original não muda.
     */
    static void organizarEmbarque(List<Tripulante> tripulantes, Scanner scanner) {
        System.out.println();
        System.out.println("--- ORGANIZAR EMBARQUE ---");
        System.out.println("Escolha o algoritmo de ordenação:");
        System.out.println("1 - Bubble Sort");
        System.out.println("2 - Insertion Sort");
        System.out.println("3 - Quick Sort");
        System.out.print("Opção: ");
        int escolha = scanner.nextInt();
        scanner.nextLine();

        // toArray cria um vetor novo, então a ordenação mexe nessa cópia
        // e a lista original continua na ordem de registro
        Tripulante[] vetor = tripulantes.toArray(new Tripulante[0]);
        EstatisticasOrdenacao est;

        if (escolha == 1) {
            est = bubbleSort(vetor, false);
        } else if (escolha == 2) {
            est = insertionSort(vetor, false);
        } else if (escolha == 3) {
            est = quickSort(vetor, false);
        } else {
            System.out.println("Opção inválida!");
            return;
        }

        System.out.println();
        System.out.println("Tripulantes ordenados por código galáctico:");
        for (int i = 0; i < vetor.length; i++) {
            System.out.printf("[%3d] %s", (i + 1), vetor[i]);
            System.out.println();
        }
        System.out.println();
        System.out.printf("Métricas: Comparações = %d | Trocas = %d | Tempo = %.4f ms",
                est.comparacoes, est.trocas, est.tempoNano / 1_000_000.0);
        System.out.println();
    }

    /*
     * Pega os 10 primeiros tripulantes (ou menos) e roda o algoritmo escolhido mostrando na tela
     * cada comparação, cada troca de assento e o total de comparações e trocas no fim de cada passagem.
     */
    static void modoPassoAPasso(List<Tripulante> tripulantes, Scanner scanner) {
        System.out.println();
        System.out.println("--- MODO PASSO A PASSO ---");
        // no máximo 10, senão a narração fica grande demais para acompanhar
        int tamanhoSub = Math.min(10, tripulantes.size());
        List<Tripulante> subLista = new ArrayList<>(tripulantes.subList(0, tamanhoSub));

        System.out.println("Utilizando os primeiros " + tamanhoSub + " tripulantes para narração orbital:");
        for (Tripulante t : subLista) {
            System.out.println(" - " + t.nome + " (" + t.codigo + ")");
        }

        System.out.println();
        System.out.println("Escolha o algoritmo para o passo a passo:");
        System.out.println("1 - Bubble Sort");
        System.out.println("2 - Insertion Sort");
        System.out.println("3 - Quick Sort");
        System.out.print("Opção: ");
        int escolha = scanner.nextInt();
        scanner.nextLine();

        // vetor novo, a lista original não é mexida
        Tripulante[] vetor = subLista.toArray(new Tripulante[0]);

        // o true liga a narração dentro do algoritmo
        if (escolha == 1) {
            bubbleSort(vetor, true);
        } else if (escolha == 2) {
            insertionSort(vetor, true);
        } else if (escolha == 3) {
            quickSort(vetor, true);
        } else {
            System.out.println("Opção inválida!");
            return;
        }

    }

    /*
     * Roda os três algoritmos com 50, 1.000 e 10.000 tripulantes, nas situações aleatório,
     * já ordenado e ordem inversa, e imprime as tabelas de tempo, comparações e trocas.
     */
    static void executarGrandeCorrida() {
        System.out.println();
        System.out.println("--- A GRANDE CORRIDA ---");

        int[] tamanhos = { 50, 1000, 10000 };
        String[] algoritmos = { "Bubble Sort", "Insertion Sort", "Quick Sort" };
        String[] situacoes = { "Aleatório", "Já ordenado", "Ordem inversa" };
        // resultados[algoritmo][situação][tamanho]
        EstatisticasOrdenacao[][][] resultados = new EstatisticasOrdenacao[3][3][3];

        for (int tIdx = 0; tIdx < tamanhos.length; tIdx++) {
            int n = tamanhos[tIdx];
            // a mesma base aleatória serve para as três situações desse tamanho
            List<Tripulante> baseAleatoria = gerarTripulantes(n);

            for (int sIdx = 0; sIdx < situacoes.length; sIdx++) {
                List<Tripulante> dados = prepararSituacao(baseAleatoria, situacoes[sIdx]);

                // cada algoritmo recebe o próprio vetor (toArray cria um novo),
                // assim um não ordena os dados antes do outro e a comparação fica justa
                resultados[0][sIdx][tIdx] = bubbleSort(dados.toArray(new Tripulante[0]), false);
                resultados[1][sIdx][tIdx] = insertionSort(dados.toArray(new Tripulante[0]), false);
                resultados[2][sIdx][tIdx] = quickSort(dados.toArray(new Tripulante[0]), false);
            }
        }

        System.out.println();
        System.out.println("[TEMPO DE EXECUÇÃO]");
        System.out.println("| Algoritmo      | Situação      | n = 50       | n = 1.000    | n = 10.000   |");
        System.out.println("|----------------|---------------|--------------|--------------|--------------|");
        // o tempo vem em nanossegundos; dividindo por 1.000.000 vira milissegundos
        for (int aIdx = 0; aIdx < algoritmos.length; aIdx++) {
            for (int sIdx = 0; sIdx < situacoes.length; sIdx++) {
                System.out.printf("| %-14s | %-13s | %9.4f ms | %9.4f ms | %9.4f ms |%n",
                        algoritmos[aIdx], situacoes[sIdx],
                        resultados[aIdx][sIdx][0].tempoNano / 1_000_000.0,
                        resultados[aIdx][sIdx][1].tempoNano / 1_000_000.0,
                        resultados[aIdx][sIdx][2].tempoNano / 1_000_000.0);
            }
        }

        System.out.println();
        System.out.println("[COMPARAÇÕES]");
        System.out.println("| Algoritmo      | Situação      | n = 50       | n = 1.000    | n = 10.000   |");
        System.out.println("|----------------|---------------|--------------|--------------|--------------|");
        for (int aIdx = 0; aIdx < algoritmos.length; aIdx++) {
            for (int sIdx = 0; sIdx < situacoes.length; sIdx++) {
                System.out.printf("| %-14s | %-13s | %12d | %12d | %12d |%n",
                        algoritmos[aIdx], situacoes[sIdx],
                        resultados[aIdx][sIdx][0].comparacoes,
                        resultados[aIdx][sIdx][1].comparacoes,
                        resultados[aIdx][sIdx][2].comparacoes);
            }
        }

        System.out.println();
        System.out.println("[TROCAS]");
        System.out.println("| Algoritmo      | Situação      | n = 50       | n = 1.000    | n = 10.000   |");
        System.out.println("|----------------|---------------|--------------|--------------|--------------|");
        for (int aIdx = 0; aIdx < algoritmos.length; aIdx++) {
            for (int sIdx = 0; sIdx < situacoes.length; sIdx++) {
                System.out.printf("| %-14s | %-13s | %12d | %12d | %12d |%n",
                        algoritmos[aIdx], situacoes[sIdx],
                        resultados[aIdx][sIdx][0].trocas,
                        resultados[aIdx][sIdx][1].trocas,
                        resultados[aIdx][sIdx][2].trocas);
            }
        }
    }

    /*
     * Monta os dados de uma situação da corrida a partir da base aleatória: deixa como está,
     * ordena crescente ou ordena e inverte. Devolve uma lista nova.
     */
    static List<Tripulante> prepararSituacao(List<Tripulante> original, String situacao) {
        List<Tripulante> lista = new ArrayList<>(original);
        if (situacao.equals("Já ordenado")) {
            Tripulante[] arr = lista.toArray(new Tripulante[0]);
            // usa o nosso próprio Quick Sort para ordenar, já que Arrays.sort é proibido
            // (as estatísticas dessa chamada são jogadas fora)
            quickSort(arr, false);
            return new ArrayList<>(List.of(arr));
        } else if (situacao.equals("Ordem inversa")) {
            Tripulante[] arr = lista.toArray(new Tripulante[0]);
            quickSort(arr, false);
            // lê o vetor ordenado de trás para frente para ficar decrescente
            List<Tripulante> invertida = new ArrayList<>(arr.length);
            for (int i = arr.length - 1; i >= 0; i--) {
                invertida.add(arr[i]);
            }
            return invertida;
        }
        return lista;
    }

    /*
     * Bubble Sort: compara vizinhos e troca quando estão fora de ordem, até não precisar mais.
     * Devolve as comparações, as trocas e o tempo.
     */
    static EstatisticasOrdenacao bubbleSort(Tripulante[] v, boolean passoAPasso) {
        EstatisticasOrdenacao est = new EstatisticasOrdenacao();
        // marca o tempo antes de ordenar; no fim, a diferença dá o tempo gasto em nanossegundos
        long inicio = System.nanoTime();
        int n = v.length;

        // cada passagem leva o maior código que sobrou para o fim do vetor
        for (int i = 0; i < n - 1; i++) {
            // se uma passagem inteira não trocar ninguém, o vetor já está ordenado
            boolean houveTroca = false;

            // no modo passo a passo, cada comparação é narrada na tela no estilo do cenário
            if (passoAPasso) {
                System.out.println();
                System.out.println("★ Bubble Sort · passagem " + (i + 1));
            }

            // o fim do vetor já está pronto, por isso vai só até n - 1 - i
            for (int j = 0; j < n - 1 - i; j++) {
                // cada par de vizinhos olhado conta uma comparação
                est.comparacoes++;
                if (passoAPasso) {
                    System.out.print(" " + v[j].nome + " (" + v[j].codigo + ") > "
                            + v[j + 1].nome + " (" + v[j + 1].codigo + ")? ");
                }

                if (v[j].codigo > v[j + 1].codigo) {
                    if (passoAPasso) {
                        System.out.println("SIM → " + v[j].nome + " e " + v[j + 1].nome + " trocam de assento!");
                    }
                    // código maior na frente: troca os dois de assento e conta uma troca
                    Tripulante troca = v[j];
                    v[j] = v[j + 1];
                    v[j + 1] = troca;
                    est.trocas++;
                    houveTroca = true;
                } else {
                    if (passoAPasso) {
                        System.out.println("NÃO → ficam onde estão.");
                    }
                }
            }

            if (passoAPasso) {
                System.out.println(" → " + v[n - 1 - i].nome + " flutua até a posição [" + (n - 1 - i) + "]");
                System.out.println(" fim da passagem " + (i + 1)
                        + " → comparações: " + est.comparacoes + " | trocas: " + est.trocas);
            }

            // parada antecipada: passagem sem troca, pode parar.
            // é isso que deixa o Bubble rápido com os dados já ordenados
            if (!houveTroca) {
                if (passoAPasso) {
                    System.out.println(" → nenhuma troca nesta passagem: a tripulação já está em ordem!");
                }
                break;
            }
        }

        est.tempoNano = System.nanoTime() - inicio;
        return est;
    }

    /*
     * Insertion Sort: pega cada tripulante e encaixa ele no lugar certo entre os que já estão ordenados à esquerda.
     * Devolve as comparações, os deslocamentos (contados como trocas) e o tempo.
     */
    static EstatisticasOrdenacao insertionSort(Tripulante[] v, boolean passoAPasso) {
        EstatisticasOrdenacao est = new EstatisticasOrdenacao();
        // mesmo esquema do Bubble: marca o tempo antes e calcula a diferença no fim
        long inicio = System.nanoTime();
        int n = v.length;

        // tudo à esquerda de i já está ordenado
        for (int i = 1; i < n; i++) {
            // a chave é o tripulante que vai ser encaixado agora
            Tripulante chave = v[i];
            int j = i - 1;

            if (passoAPasso) {
                System.out.println();
                System.out.println("★ Insertion Sort · passagem " + i
                        + " (chave: " + chave.nome + " (" + chave.codigo + "))");
            }

            // anda para a esquerda enquanto achar código maior que o da chave
            while (j >= 0) {
                // cada vez que compara com a chave conta uma comparação
                est.comparacoes++;
                if (passoAPasso) {
                    System.out.print(" " + v[j].nome + " (" + v[j].codigo + ") > "
                            + chave.nome + " (" + chave.codigo + ")? ");
                }

                if (v[j].codigo > chave.codigo) {
                    if (passoAPasso) {
                        System.out.println("SIM → " + v[j].nome + " vai para a direita!");
                    }
                    // empurra o maior uma posição para a direita (deslocamento).
                    // o Insertion não troca pares, ele desloca, então cada deslocamento conta como troca
                    // para dar para comparar com os outros dois algoritmos
                    v[j + 1] = v[j];
                    est.trocas++;
                    j--;
                } else {
                    if (passoAPasso) {
                        System.out.println("NÃO → achou o lugar de " + chave.nome + ".");
                    }
                    // achou um código menor ou igual: a chave fica logo depois dele
                    break;
                }
            }

            // coloca a chave no espaço que sobrou (essa escrita não entra na contagem)
            v[j + 1] = chave;
            if (passoAPasso) {
                System.out.println(" → " + chave.nome + " senta na posição [" + (j + 1) + "]");
                System.out.println(" fim da passagem " + i
                        + " → comparações: " + est.comparacoes + " | movimentações: " + est.trocas);
            }
        }

        est.tempoNano = System.nanoTime() - inicio;
        return est;
    }

    /*
     * Quick Sort: prepara a contagem e o tempo e chama a parte recursiva para o vetor inteiro.
     * Devolve as comparações, as trocas e o tempo.
     */
    static EstatisticasOrdenacao quickSort(Tripulante[] v, boolean passoAPasso) {
        EstatisticasOrdenacao est = new EstatisticasOrdenacao();
        long inicio = System.nanoTime();
        quickSortRec(v, 0, v.length - 1, est, passoAPasso);
        est.tempoNano = System.nanoTime() - inicio;
        return est;
    }

    /*
     * Separa o trecho em volta de um pivô e depois ordena do mesmo jeito o lado esquerdo e o direito.
     * Para quando o trecho tem 0 ou 1 elemento.
     */
    static void quickSortRec(Tripulante[] v, int inicio, int fim, EstatisticasOrdenacao est, boolean passoAPasso) {
        if (inicio < fim) {
            int posicaoPivo = separar(v, inicio, fim, est, passoAPasso);
            quickSortRec(v, inicio, posicaoPivo - 1, est, passoAPasso);
            quickSortRec(v, posicaoPivo + 1, fim, est, passoAPasso);
        }
    }

    /*
     * Partição: deixa à esquerda quem tem código menor ou igual ao do pivô, à direita quem tem maior,
     * e coloca o pivô entre os dois. Devolve a posição final do pivô.
     */
    static int separar(Tripulante[] v, int inicio, int fim, EstatisticasOrdenacao est, boolean passoAPasso) {
        // pega o primeiro elemento como pivô.
        // com dados já ordenados ou invertidos, isso deixa um dos lados vazio em toda partição
        // e o Quick Sort cai no pior caso (por volta de n² / 2 comparações)
        Tripulante pivo = v[inicio];
        // i procura da esquerda para a direita, f da direita para a esquerda
        int i = inicio + 1;
        int f = fim;

        if (passoAPasso) {
            System.out.println();
            System.out.println("★ Quick Sort · partição [" + inicio + ".." + fim + "] | Pivô: "
                    + pivo.nome + " (" + pivo.codigo + ")");
        }

        // repete até i e f se cruzarem
        while (i <= f) {
            // i avança enquanto achar código menor ou igual ao do pivô (já está do lado certo)
            while (i <= fim) {
                // cada teste contra o pivô conta uma comparação
                est.comparacoes++;
                if (passoAPasso) {
                    System.out.print(" [esq] " + v[i].nome + " (" + v[i].codigo + ") <= "
                            + pivo.nome + " (" + pivo.codigo + ")? ");
                }
                if (v[i].codigo <= pivo.codigo) {
                    if (passoAPasso) {
                        System.out.println("SIM → avança i.");
                    }
                    i++;
                } else {
                    if (passoAPasso) {
                        System.out.println("NÃO → para na esquerda.");
                    }
                    break;
                }
            }

            // f recua enquanto achar código maior que o do pivô (já está do lado certo)
            while (f > inicio) {
                // aqui também, cada teste contra o pivô conta uma comparação
                est.comparacoes++;
                if (passoAPasso) {
                    System.out.print(" [dir] " + v[f].nome + " (" + v[f].codigo + ") > "
                            + pivo.nome + " (" + pivo.codigo + ")? ");
                }
                if (v[f].codigo > pivo.codigo) {
                    if (passoAPasso) {
                        System.out.println("SIM → recua f.");
                    }
                    f--;
                } else {
                    if (passoAPasso) {
                        System.out.println("NÃO → para na direita.");
                    }
                    break;
                }
            }

            // i parou num maior e f parou num menor: os dois estão do lado errado, então troca
            if (i < f) {
                if (passoAPasso) {
                    System.out.println(" → i < f: SIM! " + v[i].nome + " (" + v[i].codigo + ") e "
                            + v[f].nome + " (" + v[f].codigo + ") trocam de assento!");
                }
                Tripulante troca = v[i];
                v[i] = v[f];
                v[f] = troca;
                est.trocas++;
                i++;
                f--;
            }
        }

        // no fim, troca o pivô com v[f] para ele ir para a posição certa.
        // essa troca também mexe no vetor, por isso entra na contagem
        // (se o pivô já estiver no lugar, não troca e não conta)
        if (inicio != f) {
            if (passoAPasso) {
                System.out.println(" → Pivô " + pivo.nome + " (" + pivo.codigo + ") troca com "
                        + v[f].nome + " (" + v[f].codigo + ") e vai para [" + f + "]!");
            }
            v[inicio] = v[f];
            v[f] = pivo;
            est.trocas++;
        } else if (passoAPasso) {
            System.out.println(" → Pivô " + pivo.nome + " (" + pivo.codigo + ") já está na posição [" + f + "].");
        }

        if (passoAPasso) {
            System.out.println(" fim da partição → comparações: " + est.comparacoes + " | trocas: " + est.trocas);
        }

        return f;
    }
}
