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

class Tripulante {
    int codigo;
    String nome;
    String planetaNatal;
    String funcao;

    public Tripulante(int codigo, String nome, String planetaNatal, String funcao) {
        this.codigo = codigo;
        this.nome = nome;
        this.planetaNatal = planetaNatal;
        this.funcao = funcao;
    }

    @Override
    public String toString() {
        return String.format("Código: %d | Nome: %-22s | Planeta: %-15s | Função: %s",
                codigo, nome, planetaNatal, funcao);
    }
}

class EstatisticasOrdenacao {
    long comparacoes;
    long trocas;
    long tempoNano;
}

public class MissaoGalactica {
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

            while (!scanner.hasNextInt()) {
                System.out.print("Por favor, digite um número válido: ");
                scanner.next();
            }

            opcao = scanner.nextInt();
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

        Random random = new Random(1990);
        Set<Integer> codigosUsados = new HashSet<>();
        List<Tripulante> tripulantes = new ArrayList<>(quantidade);

        for (int posicao = 0; posicao < quantidade; posicao++) {
            int codigo;
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

    public static List<Tripulante> carregarTripulantes(String caminhoArquivo) {
        List<Tripulante> tripulantes = new ArrayList<>();
        Path path = Path.of(caminhoArquivo);
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

    static void registrarTripulante(List<Tripulante> tripulantes, Scanner scanner) {
        System.out.println();
        System.out.println("--- REGISTRAR NOVO TRIPULANTE ---");
        System.out.print("Digite o código galáctico (8 dígitos): ");
        while (!scanner.hasNextInt()) {
            System.out.print("Código inválido. Digite um número inteiro de 8 dígitos: ");
            scanner.next();
        }
        int codigo = scanner.nextInt();
        scanner.nextLine();

        if (codigo < 10_000_000 || codigo > 99_999_999) {
            System.out.println("Erro: O código galáctico deve ter exatamente 8 dígitos!");
            return;
        }

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

    static void listarTripulantes(List<Tripulante> tripulantes) {
        System.out.println();
        System.out.println("--- TRIPULAÇÃO ATUAL ---");
        for (Tripulante tripulante : tripulantes) {
            System.out.println(tripulante);
        }
    }

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

    static void modoPassoAPasso(List<Tripulante> tripulantes, Scanner scanner) {
        System.out.println();
        System.out.println("--- MODO PASSO A PASSO ---");
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

        Tripulante[] vetor = subLista.toArray(new Tripulante[0]);

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

    static void executarGrandeCorrida() {
        System.out.println();
        System.out.println("--- A GRANDE CORRIDA ---");

        int[] tamanhos = { 50, 1000, 10000 };
        String[] algoritmos = { "Bubble Sort", "Insertion Sort", "Quick Sort" };
        String[] situacoes = { "Aleatório", "Já ordenado", "Ordem inversa" };
        EstatisticasOrdenacao[][][] resultados = new EstatisticasOrdenacao[3][3][3];

        for (int tIdx = 0; tIdx < tamanhos.length; tIdx++) {
            int n = tamanhos[tIdx];
            List<Tripulante> baseAleatoria = gerarTripulantes(n);

            for (int sIdx = 0; sIdx < situacoes.length; sIdx++) {
                List<Tripulante> dados = prepararSituacao(baseAleatoria, situacoes[sIdx]);

                resultados[0][sIdx][tIdx] = bubbleSort(dados.toArray(new Tripulante[0]), false);
                resultados[1][sIdx][tIdx] = insertionSort(dados.toArray(new Tripulante[0]), false);
                resultados[2][sIdx][tIdx] = quickSort(dados.toArray(new Tripulante[0]), false);
            }
        }

        System.out.println();
        System.out.println("[TEMPO DE EXECUÇÃO]");
        System.out.println("| Algoritmo      | Situação      | n = 50       | n = 1.000    | n = 10.000   |");
        System.out.println("|----------------|---------------|--------------|--------------|--------------|");
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

    static List<Tripulante> prepararSituacao(List<Tripulante> original, String situacao) {
        List<Tripulante> lista = new ArrayList<>(original);
        if (situacao.equals("Já ordenado")) {
            Tripulante[] arr = lista.toArray(new Tripulante[0]);
            quickSort(arr, false);
            return new ArrayList<>(List.of(arr));
        } else if (situacao.equals("Ordem inversa")) {
            Tripulante[] arr = lista.toArray(new Tripulante[0]);
            quickSort(arr, false);
            List<Tripulante> invertida = new ArrayList<>(arr.length);
            for (int i = arr.length - 1; i >= 0; i--) {
                invertida.add(arr[i]);
            }
            return invertida;
        }
        return lista;
    }

    static EstatisticasOrdenacao bubbleSort(Tripulante[] v, boolean passoAPasso) {
        EstatisticasOrdenacao est = new EstatisticasOrdenacao();
        long inicio = System.nanoTime();
        int n = v.length;

        for (int i = 0; i < n - 1; i++) {
            boolean houveTroca = false;

            if (passoAPasso) {
                System.out.println();
                System.out.println("★ Bubble Sort · passagem " + (i + 1));
            }

            for (int j = 0; j < n - 1 - i; j++) {
                est.comparacoes++;
                if (passoAPasso) {
                    System.out.print(" " + v[j].nome + " (" + v[j].codigo + ") > "
                            + v[j + 1].nome + " (" + v[j + 1].codigo + ")? ");
                }

                if (v[j].codigo > v[j + 1].codigo) {
                    if (passoAPasso) {
                        System.out.println("SIM → " + v[j].nome + " e " + v[j + 1].nome + " trocam de assento!");
                    }
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

    static EstatisticasOrdenacao insertionSort(Tripulante[] v, boolean passoAPasso) {
        EstatisticasOrdenacao est = new EstatisticasOrdenacao();
        long inicio = System.nanoTime();
        int n = v.length;

        for (int i = 1; i < n; i++) {
            Tripulante chave = v[i];
            int j = i - 1;

            if (passoAPasso) {
                System.out.println();
                System.out.println("★ Insertion Sort · passagem " + i
                        + " (chave: " + chave.nome + " (" + chave.codigo + "))");
            }

            while (j >= 0) {
                est.comparacoes++;
                if (passoAPasso) {
                    System.out.print(" " + v[j].nome + " (" + v[j].codigo + ") > "
                            + chave.nome + " (" + chave.codigo + ")? ");
                }

                if (v[j].codigo > chave.codigo) {
                    if (passoAPasso) {
                        System.out.println("SIM → " + v[j].nome + " vai para a direita!");
                    }
                    v[j + 1] = v[j];
                    est.trocas++;
                    j--;
                } else {
                    if (passoAPasso) {
                        System.out.println("NÃO → achou o lugar de " + chave.nome + ".");
                    }
                    break;
                }
            }

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

    static EstatisticasOrdenacao quickSort(Tripulante[] v, boolean passoAPasso) {
        EstatisticasOrdenacao est = new EstatisticasOrdenacao();
        long inicio = System.nanoTime();
        quickSortRec(v, 0, v.length - 1, est, passoAPasso);
        est.tempoNano = System.nanoTime() - inicio;
        return est;
    }

    static void quickSortRec(Tripulante[] v, int inicio, int fim, EstatisticasOrdenacao est, boolean passoAPasso) {
        if (inicio < fim) {
            int posicaoPivo = separar(v, inicio, fim, est, passoAPasso);
            quickSortRec(v, inicio, posicaoPivo - 1, est, passoAPasso);
            quickSortRec(v, posicaoPivo + 1, fim, est, passoAPasso);
        }
    }

    static int separar(Tripulante[] v, int inicio, int fim, EstatisticasOrdenacao est, boolean passoAPasso) {
        Tripulante pivo = v[inicio];
        int i = inicio + 1;
        int f = fim;

        if (passoAPasso) {
            System.out.println();
            System.out.println("★ Quick Sort · partição [" + inicio + ".." + fim + "] | Pivô: "
                    + pivo.nome + " (" + pivo.codigo + ")");
        }

        while (i <= f) {
            while (i <= fim) {
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

            while (f > inicio) {
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
