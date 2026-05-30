package br.edu.ac.junit;

public class Calculadora {

    public int somar(int a, int b) {
        return a + b;
    }

    public int subtrair(int a, int b) {
        return a - b;
    }

    public int multiplicar(int a, int b) {
        return a * b;
    }

    public int dividir(int dividendo, int divisor) {
        if (divisor == 0) {
            throw new IllegalArgumentException("Divisão por zero não é permitida");
        }
        return dividendo / divisor;
    }

    public boolean ehPar(int numero) {
        return numero % 2 == 0;
    }
}
