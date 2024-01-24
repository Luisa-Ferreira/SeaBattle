import java.io.File

// variaveis globais -> visiveis em todas as funções
var numLinhas = -1
var numColunas = -1

var tabuleiroHumano: Array<Array<Char?>> = emptyArray()
var tabuleiroComputador: Array<Array<Char?>> = emptyArray()

var tabuleiroPalpitesDoHumano: Array<Array<Char?>> = emptyArray()
var tabuleiroPalpitesDoComputador: Array<Array<Char?>> = emptyArray()

fun menuPrincipal(): Int {

    var opcao: Int

    do {

        println("\n> > Batalha Naval < <\n")
        println("1 - Definir Tabuleiro e Navios")
        println("2 - Jogar")
        println("3 - Gravar")
        println("4 - Ler")
        println("0 - Sair\n")

        do {
            opcao = readlnOrNull()?.toIntOrNull() ?: -1
            var nomeFicheiro = "jogo.txt"
            when (opcao) {
                1 -> {
                    opcao = menuDefinirTabuleiro()
                }

                2 -> {
                    opcao = jogar()
                }

                3 -> {
                    println("Introduza o nome do fichiro(ex: jogo.txt)")
                    nomeFicheiro = readlnOrNull() ?: "jogo.txt"
                    gravarJogo(
                        nomeFicheiro, tabuleiroHumano, tabuleiroPalpitesDoHumano,
                        tabuleiroComputador, tabuleiroPalpitesDoComputador
                    )
                }

                4 -> {
                    lerJogo(nomeFicheiro, 4)
                }

                0 -> return 0

                else -> {
                    println("!!! Opcao invalida, tente novamente")
                    opcao = -2
                }
            }
        } while (opcao == -2)

    } while (opcao == -1)

    return 1
}

fun menuDefinirTabuleiro(): Int {

    println("\n> > Batalha Naval < <\n")
    println("Defina o tamanho do tabuleiro:")
    println("Quantas linhas?")
    numLinhas = readlnOrNull()?.toIntOrNull() ?: 0

    if (numLinhas <= 0) {
        println("!!! Número de linhas invalidas, tente novamente");
        return -1
    }

    println("Quantas colunas?")
    numColunas = readlnOrNull()?.toIntOrNull() ?: 0

    if (numColunas <= 0) {
        println("!!! Número de colunas invalidas, tente novamente");
        return -1
    }

    return if (tamanhoTabuleiroValido(numLinhas, numColunas)) {
        criaTerreno(numLinhas, numColunas)
        -1
    } else {
        println("!!! Tamanho invalido, tente novamente");
        -1
    }
}

fun criaLegendaHorizontal(numColunas: Int): String {
    return ('A' until ('A' + numColunas)).joinToString(" | ") { it.toString() }
}

fun criaTerreno(nLinhas: Int, nColunas: Int) {

    val legendaHorizontal = criaLegendaHorizontal(nColunas)

    var terrenoRealString = ""

    terrenoRealString += "| $legendaHorizontal |\n"

    var linha = 1
    while (linha <= nLinhas) {
        terrenoRealString += "| "
        var coluna = 1
        while (coluna <= nColunas) {
            terrenoRealString += "~ | "
            coluna++
        }
        terrenoRealString += "$linha\n"
        linha++
    }
    print(terrenoRealString)

    tabuleiroHumano = criaTabuleiroVazio(numLinhas, numColunas)
    tabuleiroComputador = criaTabuleiroVazio(numLinhas, numColunas)

    if (menuDefinirNavios() == -1) {
        return
    }
}

fun jogar(): Int {

    if (tabuleiroPalpitesDoComputador.isEmpty()) {
        println("!!! Tem que primeiro definir o tabuleiro do jogo, tente novamente")
        return -1
    }

    print(mapa(tabuleiroPalpitesDoComputador, false))


    return 0
}

fun menuDefinirNavios(): Int {
    val barcos = calculaNumNavios(numLinhas, numColunas);

    while (barcos[0] > 0 || barcos[1] > 0 || barcos[2] > 0 || barcos[3] > 0) {

        if (barcos[0] > 0) {
            println("Insira as coordenadas de um submarino:")
        }

        var coordenadas: String?
        var orientacao: String?
        do {
            println("Coordenadas? (ex: 6,G)")
            coordenadas = readlnOrNull()


            if (coordenadas == null) {
                println("!!! Coordenadas invalidas, tente novamente")
                return -1
            } else if (coordenadas.trim() == "-1") {
                return -1
            } else if (processaCoordenadas(coordenadas, numLinhas, numColunas) == null) {
                println("!!! Coordenadas invalidas, tente novamente")
            }

            val coords = processaCoordenadas(coordenadas, numLinhas, numColunas)

        } while (coordenadas == null || coords == null)

        if (barcos[0] == 0) {

            if (barcos[1] > 0) {
                println("Insira as coordenadas de um contra-torpedeiros:")
                barcos[1]--
            } else if (barcos[2] > 0) {
                println("Insira as coordenadas de um navios-tanque:")
                barcos[1]--
            } else if (barcos[3] > 0) {
                println("Insira as coordenadas de um porta-avioes:")
                barcos[3]--
            }

            println("Insira a orientacao do navio:")
            do {
                if (tabuleiroHumano.size == 4) {
                    return 0
                }

                println("Orientacao? (N, S, E, O)")
                orientacao = readlnOrNull()

                if (orientacao == null) {
                    println("!!! Valor invalido, tente novamente")
                    return -1
                } else if (orientacao == "-1") {
                    return -1
                } else if (orientacao !in "NSEO") {
                    println("!!! Orientacao invalida, tente novamente")
                }
                print(mapa(tabuleiroHumano, true))

            } while (orientacao == null || orientacao !in "NSEO" || orientacao == "-1")

        } else {

            val coords = processaCoordenadas(coordenadas, numLinhas, numColunas)

            if (coords != null) {
                if (!insereNavioSimples(tabuleiroHumano, coords.first, coords.second, 1)) {
                    println("!!! Posicionamento invalido, tente novamente")

                } else {
                    print(mapa(tabuleiroHumano, true))
                    barcos[0]--
                }
            }
        }

    }

    preencheTabuleiroComputador(tabuleiroComputador, calculaNumNavios(numLinhas, numColunas))
    println("Pretende ver o mapa gerado para o Computador? (S/N)")
    val escolha = readLine()?.get(0)

    if (escolha == 'S') {
        print(mapa(tabuleiroComputador, true))
    }

    tabuleiroPalpitesDoComputador = criaTabuleiroVazio(numLinhas, numColunas)
    tabuleiroPalpitesDoHumano = criaTabuleiroVazio(numLinhas, numColunas)
    return 0

}

fun mapa(tabuleiro: Array<Array<Char?>>, isTabuleiroReal: Boolean): String {
    var mapaStr = ""
    val mapa = obtemMapa(tabuleiro, isTabuleiroReal)
    for (linha in mapa) {
        mapaStr += linha + "\n"
    }
    return mapaStr
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

    var coordenadasNavio: Array<Pair<Int, Int>> = emptyArray()

    if (dimensao == 1) {
        if (!coordenadaContida(tabuleiro, linha, coluna)) {
            return emptyArray()
        }
        coordenadasNavio = Array(1) { Pair(linha, coluna) }

        return coordenadasNavio
    }

    // Verifica a orientação do navio e gera as coordenadas correspondentes
    for (i in 0 until dimensao) {

        val novaLinha: Int
        val novaColuna: Int

        when (orientacao) {

            "S" -> {
                novaLinha = linha + i
                novaColuna = coluna
            }

            "N" -> {
                novaLinha = linha + i
                novaColuna = coluna
            }

            "E" -> {
                novaLinha = linha
                novaColuna = coluna + i
            }

            "O" -> {
                novaLinha = linha + i
                novaColuna = coluna
            }

            else -> throw IllegalArgumentException("Orientação inválida: $orientacao")
        }

        // Verifica se as coordenadas geradas estão dentro do tabuleiro
        if (novaLinha in 0 until tabuleiro.size && novaColuna in 0 until tabuleiro[0].size) {

        } else {
            // Se sair do tabuleiro, retorna um array vazio
            return emptyArray()
        }
    }

    return coordenadasNavio
}


fun gerarCoordenadasFronteira(
    tabuleiro: Array<Array<Char?>>,
    linha: Int,
    coluna: Int,
    orientacao: String,
    dimensao: Int
): Array<Pair<Int, Int>> {

    val coordNavio = gerarCoordenadasNavio(tabuleiro, linha, coluna, orientacao, dimensao)

    val coordenadasAoRedor: Array<Pair<Int, Int>> = Array(dimensao * 8) { Pair(0, 0) }

    if (coordNavio.isEmpty()) {
        return coordenadasAoRedor
    }

    var count = 0
    for (coord in coordNavio) {
        val (x, y) = coord

        if (coordenadaContida(tabuleiro, x - 1, y)) {
            coordenadasAoRedor[count] = Pair(x - 1, y)
            count++
        }
        if (coordenadaContida(tabuleiro, x + 1, y)) {
            coordenadasAoRedor[count] = Pair(x + 1, y)
            count++
        }
        if (coordenadaContida(tabuleiro, x, y - 1)) {
            coordenadasAoRedor[count] = Pair(x, y - 1)
            count++
        }
        if (coordenadaContida(tabuleiro, x, y + 1)) {
            coordenadasAoRedor[count] = Pair(x, y + 1)
            count++
        }
        if (coordenadaContida(tabuleiro, x + 1, y + 1)) {
            coordenadasAoRedor[count] = Pair(x + 1, y + 1)
            count++
        }
        if (coordenadaContida(tabuleiro, x - 1, y + 1)) {
            coordenadasAoRedor[count] = Pair(x - 1, y + 1)
            count++
        }
        if (coordenadaContida(tabuleiro, x + 1, y - 1)) {
            coordenadasAoRedor[count] = Pair(x + 1, y - 1)
            count++
        }

        if (coordenadaContida(tabuleiro, x - 1, y - 1)) {
            coordenadasAoRedor[count] = Pair(x - 1, y - 1)
            count++
        }

    }

// Remove coordenadas duplicadas e coordenadas fora dos limites do tabuleiro
    return limparCoordenadasVazias(coordenadasAoRedor).distinct().toTypedArray()
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

    //agua à volta disponivel gerarCoordenadasFronteira() e se esta disponivel o local
    // gerarCoordenadasNavio para tirar todas as coordenadas
    // Gera as coordenadas do navio
    val coordenadasNavio = gerarCoordenadasNavio(tabuleiro, linha, coluna, orientacao, dimensao)

    // Verifica se as coordenadas do navio estão livres
    val coordAVolta = gerarCoordenadasFronteira(tabuleiro, linha, coluna, orientacao, dimensao)

    if (coordenadasNavio.size != dimensao ||
        coordAVolta.isEmpty()
    ) {
        return false
    }

    if (!estaLivre(tabuleiro, coordenadasNavio) || !estaLivre(tabuleiro, coordAVolta)) {
        return false
    }

    for ((i, j) in coordenadasNavio) {
        tabuleiro[i - 1][j - 1] = ('0' + dimensao).toChar()
    }

    return true
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

    for (i in naviosTipo.indices) {

        val dimensao = i + 1;
        var quantidadeBarco = naviosTipo[i]

        if (naviosTipo == calculaNumNavios(numLinhas, numColunas)) {

            while (quantidadeBarco != 0) {

                var inseridoComSucesso = false
                while (!inseridoComSucesso) {
                    val orientacoes = arrayOf("N", "S", "E", "O")
                    val orientacao = orientacoes.random()

                    val numLinhas = tabuleiroComputador.size
                    val numColunas = tabuleiroComputador[0].size

                    inseridoComSucesso = insereNavio(tabuleiroComputador, numLinhas, numColunas, orientacao, dimensao)
                }

                quantidadeBarco--
            }
        }
    }
}

fun navioCompleto(tabuleiroPalpitesHumano: Array<Array<Char?>>, linha: Int, coluna: Int): Boolean {
    val numRows = tabuleiroPalpitesHumano.size
    val numCols = tabuleiroPalpitesHumano[0].size

    // Verifica se a posição está dentro dos limites do tabuleiro
    if (!coordenadaContida(tabuleiroPalpitesHumano, linha, coluna)) {
        return false
    }

    // Obtém o tipo do navio na posição especificada
    val tipoNavio = tabuleiroPalpitesHumano[linha][coluna]

    return when (tipoNavio) {
        '1' -> true  // Submarino, tamanho 1

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
//tem de ser +1 por causa da legenda horizontal
    val mapa = Array(numLinhas + 1) { "" }

    mapa[0] += "| ${criaLegendaHorizontal(numColunas)} |"

    for (i in tabuleiro.indices) {

        for (j in tabuleiro[i].indices) {
            val indSemLegenda = i + 1
            if (j == 0) {
                mapa[indSemLegenda] += "|"
            }
            if (isTabuleiroReal) {

                // Lógica para determinar o valor no tabuleiro real (ex: Submarino, Contra-torpedeiro, etc.)
                if (tabuleiro[i][j] != null) {
                    mapa[indSemLegenda] += " ${tabuleiro[i][j]} |"
                } else {
                    mapa[indSemLegenda] += " ~ |"
                }

            } else {
                // Lógica para determinar o valor no tabuleiro de palpites (ex: Água, Tiro, etc.)
                val navioQuaseAfundado = navioCompleto(tabuleiro, i, j)

                mapa[indSemLegenda] += when (tabuleiro[i][j]) {
                    'X' -> " X |"
                    '1' -> if (navioQuaseAfundado) "\u2081" else " 1 |"
                    '2' -> if (navioQuaseAfundado) "\u2082" else " 2 |"
                    '3' -> if (navioQuaseAfundado) "\u2083" else " 3 |"
                    '4' -> if (navioQuaseAfundado) "\u2084" else " 4 |"
                    else -> " ? |"
                }
            }

            if (j == tabuleiro[i].size - 1) {
                mapa[indSemLegenda] += " ${indSemLegenda}"
            }
        }

    }
    return mapa
}


fun calculaNaviosFaltaAfundar(tabuleiroPalpites: Array<Array<Char?>>): Array<Int> {
    val naviosFaltaAfundar = Array(4) { 0 }

    for (linha in tabuleiroPalpites.indices) {
        for (coluna in tabuleiroPalpites[0].indices) {
            val palpite = tabuleiroPalpites[linha][coluna]
            if (palpite != null && palpite != 'X') {
                navioCompleto(tabuleiroPalpites, linha, coluna)
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
            tabuleiroPalpitesHumano[linha][coluna] = alvo
            tabuleiroComputador[linha][coluna] = 'X'
            "Agua." // Água
        }

        '1' -> {
            tabuleiroPalpitesHumano[linha][coluna] = alvo
            tabuleiroComputador[linha][coluna] = 'X'
            "Tiro num submarino." // Tiro num submarino
        }

        '2' -> {
            tabuleiroPalpitesHumano[linha][coluna] = alvo
            tabuleiroComputador[linha][coluna] = 'X'
            "Tiro num contra-torpedeiro." // Tiro num contra-torpedeiro
        }

        '3' -> {
            tabuleiroPalpitesHumano[linha][coluna] = alvo
            tabuleiroComputador[linha][coluna] = 'X'
            "Tiro num navio-tanque." // Tiro num navio-tanque
        }

        '4' -> {
            tabuleiroPalpitesHumano[linha][coluna] = alvo
            tabuleiroComputador[linha][coluna] = 'X'
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

    var count = 0

    while (count < 4) {
        if (contarNaviosDeDimensao(tabuleiroPalpites, count + 1) != naviosDimensao[count]) {
            return false
        }
        count++
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
    val tamanho = tabuleiroPalpitesComputador.size
    println("Tabuleiro ${tamanho}x${tamanho} gravado com sucesso.\n")
}

fun criaTabuleiroVazio(numLinhas: Int, numColunas: Int): Array<Array<Char?>> {
    //cria o tabuleiro com todas as posicoes vazias (null) do nosso tabuleiro
    return Array(numLinhas) { Array(numColunas) { null } }
}

fun main() {

//    navioCompleto()
//
//    lancarTiro()
//
//    contarNaviosDeDimensao()
//calculaNaviosFaltaAfundar()
// calculaEstatisticas()
//    val naviosTipo= arrayOf(1,2,3,4)
//    preencheTabuleiroComputador(tabuleiroComputador,naviosTipo)
    //  obtemMapa(tabuleiroHumano,true)

    // tabuleiroPalpitesDoHumano = criaTabuleiroVazio(4, 4)


    //navioCompleto(tabuleiroPalpitesDoHumano, 1, 2)
//    for (elemento in obtemMapa(tabuleiroHumano, false)) {
//        println(elemento)
//    }
//    preencheTabuleiroComputador(tabuleiroHumano, arrayOf(2, 0, 1, 3))
//    insereNavioSimples(tabuleiroHumano, 1, 1, 1)
    menuPrincipal();

}