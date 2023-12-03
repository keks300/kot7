import java.util.concurrent.locks.ReentrantLock

var philosophersFinishedEating = 0 // Переменная для отслеживания количества философов, закончивших обед
var countPhilosopher = 0 // Переменная для хранения общего числа философов

class Philosopher(val id: String, val leftFork: ReentrantLock, val rightFork: ReentrantLock) : Runnable {
    override fun run() {
        eat() // Философ начинает обедать при вызове метода run()
    }

    private fun eat() {
        var tut = true // Флаг для управления циклом обедающего философа
        while (tut) {
            when ((1..2).random()) { // Случайный выбор вилки (1 - левая, 2 - правая)
                1 -> {
                    if (rightFork.tryLock()) { // Попытка взять правую вилку
                        try {
                            println("Философ $id обедает левой вилкой.")
                            tut = false // Обед завершен, устанавливаем флаг в false
                            philosophersFinishedEating++ // Увеличиваем счетчик закончивших обедать философов
                        } finally {
                            rightFork.unlock() // Всегда освобождаем вилку в блоке finally
                        }
                    } else if (leftFork.tryLock()) { // Если правая вилка занята, попытка взять левую вилку
                        try {
                            println("Философ $id обедает правой вилкой.")
                            tut = false
                            philosophersFinishedEating++
                        } finally {
                            leftFork.unlock()
                        }
                    } else {
                        println("Философ $id размышляет")
                        tut = false // Философ не смог взять вилку, переходит в режим размышлений
                    }
                }

                2 -> {
                    if (leftFork.tryLock()) {
                        try {
                            println("Философ $id обедает правой вилкой.")
                            tut = false
                            philosophersFinishedEating++
                        } finally {
                            leftFork.unlock()
                        }
                    } else if (rightFork.tryLock()) {
                        try {
                            println("Философ $id обедает левой вилкой.")
                            tut = false
                            philosophersFinishedEating++
                        } finally {
                            rightFork.unlock()
                        }
                    } else {
                        println("Философ $id размышляет")
                        tut = false
                    }
                }
            }
        }
    }
}


fun main() {
    println("Сколько философов за круглым столом: ")
    print("Введите целое число: ")
    countPhilosopher = enter().countPhilosopher // Инициализация переменной countPhilosopher введенным числом

    var name = Array(countPhilosopher) {""} // Создается массив для хранения имен философов

    // Цикл для ввода имен каждого философа
    for (i in 0 until countPhilosopher){
        print("Имя философа ${i+1}: ")
        name[i] = readln() // Запись введенного имени в соответствующую ячейку массива
    }
    println("_________________________")

    // Создается список вилок (ReentrantLock) в количестве, равном числу философов
    val forks = List(countPhilosopher) { ReentrantLock() }

    // Создается список философов с соответствующими именами и парами вилок
    val philosophers = List(countPhilosopher) { id ->
        Philosopher(
            name[id],
            forks[id],
            forks[(id + 1) % forks.size]
        )
    }

    // Создается список потоков, каждый из которых связан с соответствующим философом
    val threads = philosophers.map { Thread(it) }

    // Запускаются все потоки
    threads.forEach { it.start() }

    // Ожидание завершения всех потоков
    threads.forEach { it.join() }

    // Проверка, завершили ли все философы обед
    if (countPhilosopher == philosophersFinishedEating){
        println("Все философы обедают")
    }
}


class enter {
    var countPhilosopher = 0 // Переменная для хранения введенного пользователем числа философов

    init {
        var valid = false // Флаг для определения, является ли введенное значение целым числом
        do {
            try {
                countPhilosopher = readLine()?.toInt() ?: 0 // Считывание ввода пользователя и попытка преобразовать его в целое число
                valid = true // Если преобразование прошло успешно, устанавливаем флаг в true
            } catch (e: NumberFormatException) {
                println("Ошибка. Введите целое число.") // Вывод сообщения об ошибке, если ввод не является целым числом
            }
        } while (!valid) // Повторять цикл, пока не будет введено корректное целое число
    }
}