import java.io.File

// Constantes Menu Principal
const val MENU_PRINCIPAL = 100
const val MENU_DEFINIR_TABULEIRO = 101
const val MENU_DEFINIR_NAVIOS = 102
const val MENU_JOGAR = 103
const val MENU_LER_FICHEIRO = 104
const val MENU_GRAVAR_FICHEIRO = 105
const val SAIR = 106

// variaveis globais -> visiveis em todas as funções
var numLinhas = -1
var numColunas = -1

var tabuleiroHumano: Array<Array<Char?>> = emptyArray()
var tabuleiroComputador: Array<Array<Char?>> = emptyArray()

var tabuleiroPalpitesDoHumano: Array<Array<Char?>> = emptyArray()
var tabuleiroPalpitesDoComputador: Array<Array<Char?>> = emptyArray()

fun menuPrincipal(): Int {

    println("")
    println("> > Batalha Naval < <")
    println("")
    println("1 - Definir Tabuleiro e Navios")
    println("2 - Jogar")
    println("3 - Gravar")
    println("4 - Ler")
    println("0 - Sair")
    println("")

    val opcao = readlnOrNull()?.toIntOrNull() ?: -1

    return when (opcao) {

        1 -> MENU_DEFINIR_TABULEIRO
        2 -> MENU_JOGAR
        3 -> MENU_GRAVAR_FICHEIRO
        4 -> MENU_LER_FICHEIRO
        0 -> SAIR

        else -> {

            println("!!! Opcao invalida, tente novamente")

            MENU_PRINCIPAL

        }
    }
}

fun menuDefinirTabuleiro(): Int {

    println("\n> > Batalha Naval < <\n")
    println("Defina o tamanho do tabuleiro:")
    println("Quantas linhas?")
    numLinhas = readlnOrNull()?.toIntOrNull() ?: 0

    if (numLinhas <= 0) {
        return MENU_PRINCIPAL
    }

    println("Quantas colunas?")
    numColunas = readlnOrNull()?.toIntOrNull() ?: 0

    if (numColunas <= 0) {
        return MENU_DEFINIR_TABULEIRO
    }

    if (tamanhoTabuleiroValido(numLinhas, numColunas)) {
        criaTerreno(numLinhas, numColunas)
        return MENU_DEFINIR_NAVIOS
    } else {
        return MENU_DEFINIR_TABULEIRO
    }
}

fun criaLegendaHorizontal(numColunas: Int): String {
    println("")
    val letras = ('A' until ('A' + numColunas)).joinToString(" | ") { it.toString() }
    return letras
}

fun criaTerreno(numLinhas: Int, numColunas: Int) {
    val legendaHorizontal = criaLegendaHorizontal(numColunas)
    println(legendaHorizontal)

    var linha = 1
    while (linha <= numLinhas) {
        print(" | ")
        var coluna = 1
        while (coluna <= numColunas) {
            print(" ~ | ")
            coluna++
        }
        println(" $linha")
        linha++
    }
}

fun menuDefinirNavios(): Int {
    println("")
    println("Insira as coordenadas do navio:")
    var coordenadas: String?
    var orientacao: String?
    do {
        println("Coordenadas? (ex: 6,G)")
        coordenadas = readlnOrNull()
        if (coordenadas == null) {
            println("!!! Coordenadas invalidas, tente novamente")
            return MENU_PRINCIPAL
        } else if (coordenadas.trim() == "-1") {
            return MENU_PRINCIPAL
        } else if (processaCoordenadas(coordenadas, numLinhas, numColunas) == null) {
            println("!!! Coordenadas invalidas, tente novamente")
        }
    } while (coordenadas == null || processaCoordenadas(coordenadas, numLinhas, numColunas) == null)

    println("Insira a orientacao do navio:")
    do {
        println("Orientacao? (N, S, E, O)")
        orientacao = readlnOrNull()
        if (orientacao == null) {
            println("!!! Valor invalido, tente novamente")
            return MENU_PRINCIPAL
        } else if (orientacao == "-1") {
            return MENU_PRINCIPAL
        } else if (orientacao !in "NSEO") {
            println("!!! Orientacao invalida, tente novamente")
        }
    } while (orientacao == null || orientacao !in "NSEO" || orientacao == "-1")
    return MENU_PRINCIPAL
}


fun tamanhoTabuleiroValido(numLinhas: Int, numColunas: Int): Boolean {
    return (numLinhas == 4 || numLinhas == 5 || numLinhas == 7 || numLinhas == 8 || numLinhas == 10) && numLinhas == numColunas
}

fun processaCoordenadas(coordenadas: String, numLinhas: Int, numColunas: Int): Pair<Int, Int>? {

    // Dividir a string de coordenadas usando ',' como delimitador
    val partes = coordenadas.split(",")

    // Verificar se há exatamente duas partes após a divisão
    if (partes.size == 2) {
        // Tentar converter a primeira parte para um número inteiro (linha)
        val linha = partes[0].trim().toIntOrNull()

        // Verificar se a conversão foi bem-sucedida e está dentro dos limites do tabuleiro
        if (linha != null && linha in 1..numLinhas) {
            // Extrair a primeira letra da segunda parte (coluna)
            val colunaLetra = partes[1].trim().firstOrNull()

            // Verificar se a extração foi bem-sucedida
            if (colunaLetra != null) {
                // Converter a letra da coluna para um número
                val colunaNum = colunaLetra.toInt() - 'A'.toInt() + 1

                // Verificar se a coluna está dentro dos limites do tabuleiro
                if (colunaNum in 1..numColunas) {
                    // Coordenadas válidas, retornar o par (linha, coluna)
                    return Pair(linha, colunaNum)
                }
            }
        }
    }

    // Coordenadas inválidas, retornar null
    return null
}

fun calculaNumNavios(numLinhas: Int, numColunas: Int): Array<Int> {

    /*
    Explicação da função:

        Esta funçao é para sabermos conforme o tamanho do nosso tabuleiro quantos navios temos de inserir.
        Pois 4x4 sao 2 submarinos, em 5x5 é 1 submarinos e 1 contra-torpedeiros, 7x7 2 submarinos e 1 contra-torpedeiros
        Entao vamos inserir as linhas e as colunas e vamos retornar uma array em que a

        posicao [0]-> nº de submarinos
        posiçao [1]-> nºcontra-torpedeiros
        posicao [2]-> nºnavios-tanque
        posicao [3]-> nºporta-avioes
        arrayof(numSubmarinos, numContraTorpedeiros, numNaviosTanque, numPortaAvioes)

        caso seja introduzido coordenadas invalidas entao retorna-se numa array vazia
    */

    if (numLinhas != numColunas) {
        return emptyArray()
    }

    return when (numLinhas) {
        4 -> arrayOf(2, 0, 0, 0)
        5 -> arrayOf(1, 1, 1, 0)
        7 -> arrayOf(2, 1, 1, 1)
        8 -> arrayOf(2, 2, 1, 1)
        10 -> arrayOf(3, 2, 1, 1)
        else -> arrayOf()
    }
}

fun coordenadaContida(tabuleiro: Array<Array<Char?>>, linha: Int, coluna: Int): Boolean {
    /*
        Util para verificar coordenadas!!
        Esta função verifica se uma linha e uma coluna estão contidas dentro do nosso tabuleiro
        Retorna true se estiverem contidas e false se não estiverem
     */

    return linha in 1..tabuleiro.size && coluna in 1..tabuleiro[0].size
}

fun limparCoordenadasVazias(coordenadas: Array<Pair<Int, Int>>): Array<Pair<Int, Int>> {

    // Filtra as coordenadas para remover aquelas que são vazias (0, 0)
    val coordenadasNaoVazias = coordenadas.filter { it.first != 0 || it.second != 0 }

    // Converte a lista resultante para um array
    return coordenadasNaoVazias.toTypedArray()
}

fun juntarCoordenadas(
    coordenadas1: Array<Pair<Int, Int>>,
    coordenadas2: Array<Pair<Int, Int>>
): Array<Pair<Int, Int>> {
    // Concatena (junta) as arrays passadas por argumento (coordenadas1 e coordenadas2)
    val coordenadasConcatenadas = coordenadas1 + coordenadas2

    // Usa distinct para eliminar quaisquer elementos duplicados
    val coordenadasUnicas = coordenadasConcatenadas.distinct()

    // Converte a lista resultante para um array
    return coordenadasUnicas.toTypedArray()
}

fun gerarCoordenadasNavio(
    tabuleiro: Array<Array<Char?>>,
    linha: Int,
    coluna: Int,
    orientacao: String,
    dimensao: Int
): Array<Pair<Int, Int>> {
    val coordenadasNavio = mutableListOf<Pair<Int, Int>>()

    // Verifica a orientação do navio e gera as coordenadas correspondentes
    for (i in 0 until dimensao) {
        val novaLinha: Int
        val novaColuna: Int

        when (orientacao) {
            "horizontal" -> {
                novaLinha = linha
                novaColuna = coluna + i
            }
            "vertical" -> {
                novaLinha = linha + i
                novaColuna = coluna
            }
            else -> throw IllegalArgumentException("Orientação inválida: $orientacao")
        }

        // Verifica se as coordenadas geradas estão dentro do tabuleiro
        if (novaLinha in 0 until tabuleiro.size && novaColuna in 0 until tabuleiro[0].size) {
            coordenadasNavio.add(Pair(novaLinha, novaColuna))
        } else {
            // Se sair do tabuleiro, retorna um array vazio
            return emptyArray()
        }
    }

    return coordenadasNavio.toTypedArray()
}

fun gerarCoordenadasFronteira(
    tabuleiro: Array<Array<Char?>>,
    linha: Int,
    coluna: Int,
    orientacao: String,
    dimensao: Int
): Array<Pair<Int, Int>> {

    val coordenadasNavio = gerarCoordenadasNavio(tabuleiro, linha, coluna, orientacao, dimensao)
    val coordenadasFronteira = mutableListOf<Pair<Int, Int>>()

    for ((navioLinha, navioColuna) in coordenadasNavio) {
        for (i in -1..1) {
            val novaLinha = navioLinha + i * (if (orientacao == "N") -1 else if (orientacao == "S") 1 else 0)
            for (j in -1..1) {
                val novaColuna = navioColuna + j * (if (orientacao == "W") -1 else if (orientacao == "E") 1 else 0)
                coordenadasFronteira.add(Pair(novaLinha, novaColuna))
            }
        }
    }

    // Remove coordenadas duplicadas e coordenadas fora dos limites do tabuleiro
    return limparCoordenadasVazias(coordenadasFronteira.toTypedArray()).distinct().toTypedArray()
}

fun estaLivre(tabuleiro: Array<Array<Char?>>, coordenadas: Array<Pair<Int, Int>>): Boolean {
    for ((linha, coluna) in coordenadas) {
        // Verifica se as coordenadas estão dentro dos limites do tabuleiro
        if (!coordenadaContida(tabuleiro, linha, coluna)) {
            return false
        }

        // Verifica se a célula está ocupada
        if (tabuleiro[linha - 1][coluna - 1] != null) {
            return false
        }
    }
    return true
}

fun insereNavioSimples(tabuleiro: Array<Array<Char?>>, linha: Int, coluna: Int, dimensao: Int): Boolean {
    val orientacao = "E"  // Sempre na orientação leste

    // Gera as coordenadas do navio
    val coordenadasNavio = gerarCoordenadasNavio(tabuleiro, linha, coluna, orientacao, dimensao)

    // Verifica se as coordenadas do navio estão livres
    if (estaLivre(tabuleiro, coordenadasNavio)) {
        // Insere o navio no tabuleiro
        for ((i, j) in coordenadasNavio) {
            tabuleiro[i - 1][j - 1] = 'N'  // 'N' representa um navio simples
        }
        return true
    }

    return false
}

fun insereNavio(
    tabuleiro: Array<Array<Char?>>,
    numLinhas: Int,
    numColunas: Int,
    orientacao: String,
    dimensao: Int
): Boolean {
    // Obtém as coordenadas do navio e da fronteira
    val coordenadasNavio = gerarCoordenadasNavio(tabuleiro, numLinhas, numColunas, orientacao, dimensao)
    val fronteira = gerarCoordenadasFronteira(tabuleiro, numLinhas, numColunas, orientacao, dimensao)

    // Junta as coordenadas do navio e da fronteira
    val coordenadasFinais = juntarCoordenadas(coordenadasNavio, fronteira)

    // Verifica se todas as coordenadas estão livres
    if (estaLivre(tabuleiro, coordenadasFinais)) {
        // Insere o navio no tabuleiro
        for ((linha, coluna) in coordenadasFinais) {
            tabuleiro[linha - 1][coluna - 1] = 'N'  // Pode ajustar para outro caractere que represente um navio
        }
        return true
    }

    return false
}

fun preencheTabuleiroComputador(
    tabuleiroComputador: Array<Array<Char?>>,
    naviosTipo: Array<Int>
) {
    /*
    Gera o tabuleiro do computador, recebe um array de 4 posiçoes Arrayof(Submarinos, ContraTorpedeiros,NaviosTanque,PortaAvioes)
    Esta funçao nao retorna nada pois so tem como funçao preencher de forma ALEATORIA o tabuleiroComputador.
    Contudo verificar se as coordenadas aleatorias:
    - Se estão dentro dos limites do tabuleiro
    - Não estão em cima dos outros navios
    - Nem em cima da água (posições à volta) dos outros navios
    */

    val tiposNavios = arrayOf("S", "C", "T", "P")  // Submarino, Contra-Torpedeiro, Navio Tanque, Porta-Aviões

    for (tipoNavio in tiposNavios) {
        val dimensao = when (tipoNavio) {
            "S" -> 1
            "C" -> 2
            "T" -> 3
            "P" -> 4
            else -> 1  // Padrão para outros tipos
        }

        // Tenta inserir o navio de forma aleatória até conseguir
        var inseridoComSucesso = false
        while (!inseridoComSucesso) {
            val orientacoes = arrayOf("N", "S", "E", "O")
            val orientacao = orientacoes.random()

            val numLinhas = tabuleiroComputador.size
            val numColunas = tabuleiroComputador[0].size

            inseridoComSucesso = insereNavio(tabuleiroComputador, numLinhas, numColunas, orientacao, dimensao)
        }
    }
}

fun navioCompleto(tabuleiroPalpitesHumano: Array<Array<Char?>>, linha: Int, coluna: Int): Boolean {
    val numRows = tabuleiroPalpitesHumano.size
    val numCols = tabuleiroPalpitesHumano[0].size

    // Verifica se a posição está dentro dos limites do tabuleiro
    if (linha < 0 || linha >= numRows || coluna < 0 || coluna >= numCols) {
        return false
    }

    // Obtém o tipo do navio na posição especificada
    val tipoNavio = tabuleiroPalpitesHumano[linha][coluna]

    return when (tipoNavio) {
        'S' -> true  // Submarino, tamanho 1
        'C' -> {
            // Contra-torpedeiro, pode ser de tamanho 1 ou 2
            val tamanho = when {
                linha + 1 < numRows && tabuleiroPalpitesHumano[linha + 1][coluna] == 'C' -> 2
                else -> 1
            }

            // Verifica se o contra-torpedeiro está completamente afundado
            (0 until tamanho).all { i ->
                linha + i < numRows && tabuleiroPalpitesHumano[linha + i][coluna] == 'C'
            }
        }
        else -> false  // Nenhum navio
    }
}

fun obtemMapa(tabuleiro: Array<Array<Char?>>, isTabuleiroReal: Boolean): Array<String> {
    /*
        isTabuleiroReal é para ir true se o tabuleiro for o real e false se for o palpites
        pois se for real é para ser representado com ~ nas casas vazias e no de palpites
        é representado com ? e os numeros pequenos caso o navio esteja quase para se afundar (usar a funçao naviocompleto())

        Para representar os numeros pequenos usar os codigos Unicode '\u2081' a '\u2084'

        '\u2081'-> 1 pequeno
        '\u2082'-> 2 pequeno
        '\u2084'-> 4 pequeno

        deve de retornar um arrayString com o tabuleiro pedido. chamar as funçoes criaLegendaHorizontal e cria terreno para o real
     */

    val mapa: MutableList<String> = mutableListOf()

    // Adiciona a legenda horizontal ao mapa
    mapa.add(criaLegendaHorizontal(tabuleiro[0].size))

    for (i in tabuleiro.indices) {
        // Adiciona o número da linha ao início de cada linha do mapa
        val linhaMapa = mutableListOf<String>()
        linhaMapa.add("${i + 1} |")

        for (j in tabuleiro[i].indices) {
            val valor = if (isTabuleiroReal) {
                // Lógica para determinar o valor no tabuleiro real (ex: Submarino, Contra-torpedeiro, etc.)
                tabuleiro[i][j]?.toString() ?: "~"  // Usa "~" para representar casas vazias
            } else {
                // Lógica para determinar o valor no tabuleiro de palpites (ex: Água, Tiro, etc.)
                val navioQuaseAfundado = navioCompleto(tabuleiro, i, j)

                when (tabuleiro[i][j]) {
                    null -> "?"
                    'S' -> if (navioQuaseAfundado) "\u2081" else "?"
                    'C' -> if (navioQuaseAfundado) "\u2082" else "?"
                    'T' -> if (navioQuaseAfundado) "\u2083" else "?"
                    'P' -> if (navioQuaseAfundado) "\u2084" else "?"
                    else -> "?"
                }
            }

            // Adiciona o valor ao mapa
            linhaMapa.add(" $valor |")
        }

        // Adiciona a linha do mapa à lista do mapa
        mapa.add(linhaMapa.joinToString(""))
    }

    return mapa.toTypedArray()
}

fun calculaNaviosFaltaAfundar(tabuleiroPalpites: Array<Array<Char?>>): Array<Int> {
    val naviosFaltaAfundar = Array(4) { 0 }

    for (linha in tabuleiroPalpites.indices) {
        for (coluna in tabuleiroPalpites[0].indices) {
            val palpite = tabuleiroPalpites[linha][coluna]
            if (palpite != null && palpite != 'X') {
                when (palpite) {
                    'P' -> naviosFaltaAfundar[0]++
                    'T' -> naviosFaltaAfundar[1]++
                    'C' -> naviosFaltaAfundar[2]++
                    'S' -> naviosFaltaAfundar[3]++
                }
            }
        }
    }

    return naviosFaltaAfundar
}

fun lancarTiro(
    tabuleiroComputador: Array<Array<Char?>>,
    tabuleiroPalpitesHumano: Array<Array<Char?>>,
    coordenadas: Pair<Int, Int>
): String {

    val linha = coordenadas.first - 1
    val coluna = coordenadas.second - 1

    if (linha < 0 || coluna < 0 || linha >= tabuleiroComputador.size || coluna >= tabuleiroComputador[0].size) {
        // Se as coordenadas do tiro estiverem fora do tabuleiro, retorna string vazia (inválido)
        return ""
    }

    val alvo = tabuleiroComputador[linha][coluna]

    return when (alvo) {
        null -> {
            tabuleiroPalpitesHumano[linha][coluna] = 'X'
            "Agua." // Água
        }

        'S' -> {
            tabuleiroPalpitesHumano[linha][coluna] = 'S'
            "Tiro num submarino." // Tiro num submarino
        }

        'C' -> {
            tabuleiroPalpitesHumano[linha][coluna] = 'C'
            "Tiro num contra-torpedeiro." // Tiro num contra-torpedeiro
        }

        'T' -> {
            tabuleiroPalpitesHumano[linha][coluna] = 'T'
            "Tiro num navio-tanque." // Tiro num navio-tanque
        }

        'P' -> {
            tabuleiroPalpitesHumano[linha][coluna] = 'P'
            "Tiro num porta-avioes." // Tiro num porta-aviões
        }

        else -> {
            "" // Tiro repetido
        }
    }
}

fun geraTiroComputador(tabuleiroPalpitesComputador: Array<Array<Char?>>): Pair<Int, Int> {

    /*
        tem de gerar ALEATORIAMENTE um tiro que:
        - esteja a null no tabuleiroPalpitesComputador
        - e que seja valido (que esteja dentro dos limites do tabuleiro
    */

    while (true) {
        val numLinhas = (1..tabuleiroPalpitesComputador.size).random()
        val numColunas = (1..tabuleiroPalpitesComputador[0].size).random()

        if (tabuleiroPalpitesComputador[numLinhas - 1][numColunas - 1] == null) {
            return Pair(numLinhas, numColunas)
        }
    }
}

fun contarNaviosDeDimensao(tabuleiroPalpites: Array<Array<Char?>>, dimensao: Int): Int {

    /*
        A partir da dimensao (ex: 2-> contra-torpedeiros) sabemos qual é o tipo de barco que queremos contar.
        Só contamos como 1 quando esse barco está totalmente afundado(todas as coordenadas levaram tiro)
        Se nao houver navios desse tipo com tiros em todas as coordenadas entao retorna 0
     */

    var contador = 0

    for (numLinhas in tabuleiroPalpites.indices) {
        for (numColunas in tabuleiroPalpites[0].indices) {
            if (navioCompleto(tabuleiroPalpites, numLinhas + 1, numColunas + 1)) {
                contador++
            }
        }
    }

    return contador
}

fun venceu(tabuleiroPalpites: Array<Array<Char?>>): Boolean {
    /*
        Verifica se o jogador venceu, ou seja, se afundou todos os navios do adversário.
        Retorna true se o jogador venceu, false caso contrário.
    */

    val naviosDimensao = calculaNumNavios(numLinhas, numColunas)

    for (count in 1..4) {
        if (contarNaviosDeDimensao(tabuleiroPalpites, count) != naviosDimensao[count]) {
            return false
        }
    }

    return true
}

fun calculaEstatisticas(tabuleiroPalpites: Array<Array<Char?>>): Array<Int> {
    val estatisticas = Array(3) { 0 } // Inicializa um array de tamanho 3 com valores zero

    for (linha in tabuleiroPalpites.indices) {
        for (coluna in tabuleiroPalpites[0].indices) {
            val palpite = tabuleiroPalpites[linha][coluna]

            if (palpite != null) {
                estatisticas[0]++ // Incrementa o número de jogadas

                if (palpite == 'X') {
                    estatisticas[1]++ // Incrementa o número de tiros certeiros
                }

                if (navioCompleto(tabuleiroPalpites, linha + 1, coluna + 1)) {
                    estatisticas[2]++ // Incrementa o número de navios afundados
                }
            }
        }
    }

    return estatisticas
}


fun lerJogo(nomeFicheiro: String, tipoDeTabuleiro: Int): Array<Array<Char?>> {
    /*
        Lê o jogo a partir de um arquivo e retorna o tabuleiro correspondente.
        O parâmetro 'tipoDeTabuleiro' determina qual tabuleiro deve ser retornado:
        - 1: tabuleiroRealHumano
        - 2: tabuleiroPalpitesHumano
        - 3: tabuleiroRealComputador
        - 4: tabuleiroPalpitesComputador
    */

    val linhas = File(nomeFicheiro).readLines()

    val tabuleiro = Array(linhas.size) { Array(linhas[0].length) { null as Char? } }

    for (i in linhas.indices) {
        for (j in linhas[i].indices) {
            tabuleiro[i][j] = linhas[i][j]
        }
    }

    return when (tipoDeTabuleiro) {
        1 -> tabuleiroHumano
        2 -> tabuleiroPalpitesDoHumano
        3 -> tabuleiroComputador
        4 -> tabuleiroPalpitesDoComputador
        else -> emptyArray()  // Retorna um array vazio se o tipo de tabuleiro for inválido
    }
}


fun gravarJogo(
    nomeFicheiro: String,
    tabuleiroRealHumano: Array<Array<Char?>>,
    tabuleiroPalpitesHumano: Array<Array<Char?>>,
    tabuleiroRealComputador: Array<Array<Char?>>,
    tabuleiroPalpitesComputador: Array<Array<Char?>>
) {

    val conteudo = StringBuilder()

    conteudo.append("${numLinhas}x$numColunas\n") // Adicionar a dimensão na primeira linha
    conteudo.append("Jogador\n")

    // Adicionar o tabuleiro real do humano
    for (linha in tabuleiroRealHumano) {
        conteudo.append(linha.joinToString("") + "\n")
    }

    // Adicionar uma linha em branco entre os tabuleiros
    conteudo.append("\n")

    // Adicionar o tabuleiro de palpites do humano
    for (linha in tabuleiroPalpitesHumano) {
        conteudo.append(linha.joinToString("") + "\n")
    }

    // Adicionar uma linha em branco entre os tabuleiros
    conteudo.append("\n")

    conteudo.append("Computador\n")

    // Adicionar o tabuleiro real do computador
    for (linha in tabuleiroRealComputador) {
        conteudo.append(linha.joinToString("") + "\n")
    }

    // Adicionar uma linha em branco entre os tabuleiros
    conteudo.append("\n")

    // Adicionar o tabuleiro de palpites do computador
    for (linha in tabuleiroPalpitesComputador) {
        conteudo.append(linha.joinToString("") + "\n")
    }

    // Gravar o conteúdo no arquivo
    File(nomeFicheiro).writeText(conteudo.toString())
    println("Tabuleiro gravado com sucesso.")
}

fun criaTabuleiroVazio(numLinhas: Int, numColunas: Int): Array<Array<Char?>> {
    //cria o tabuleiro com todas as posicoes vazias (null) do nosso tabuleiro
    return Array(numLinhas) { Array(numColunas) { null } }
}

fun main() {

//    val nomeFicheiro = "jogo.txt"
//
//    var menuActual = MENU_PRINCIPAL
//
//    while (true) {
//
//        menuActual = when (menuActual) {
//
//            MENU_PRINCIPAL -> menuPrincipal()
//            MENU_DEFINIR_TABULEIRO -> menuDefinirTabuleiro()
//            MENU_DEFINIR_NAVIOS -> menuDefinirNavios()
//            //MENU_JOGAR -> jogar()
//            //MENU_LER_FICHEIRO -> lerJogo("jogo.txt", 1)
//            MENU_GRAVAR_FICHEIRO -> gravarJogo("jogo.txt", tabuleiroHumano, tabuleiroPalpitesDoHumano, tabuleiroComputador, tabuleiroPalpitesDoComputador)
//            SAIR -> return
//            else -> return
//        }
//
//    }

}