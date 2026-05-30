package br.edu.ac.junit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class CalculadoraTest {

    private Calculadora calculadora;

    @BeforeEach
    void setUp() {
        calculadora = new Calculadora();
    }

    @Test
    @DisplayName("Deve somar dois números inteiros")
    void deveSomar() {
        Assertions.assertEquals(7, calculadora.somar(3, 4));
    }

    @Test
    @DisplayName("Deve subtrair dois números inteiros")
    void deveSubtrair() {
        Assertions.assertEquals(1, calculadora.subtrair(4, 3));
    }

    @Test
    @DisplayName("Deve multiplicar dois números inteiros")
    void deveMultiplicar() {
        Assertions.assertEquals(12, calculadora.multiplicar(3, 4));
    }

    @ParameterizedTest(name = "{0} / {1} = {2}")
    @CsvSource({
            "10, 2, 5",
            "9,  3, 3",
            "20, 4, 5"
    })
    @DisplayName("Deve dividir corretamente vários pares de valores")
    void deveDividir(int dividendo, int divisor, int esperado) {
        Assertions.assertEquals(esperado, calculadora.dividir(dividendo, divisor));
    }

    @Test
    @DisplayName("Deve lançar exceção ao dividir por zero")
    void deveLancarExcecaoAoDividirPorZero() {
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> calculadora.dividir(10, 0));
        Assertions.assertEquals("Divisão por zero não é permitida", ex.getMessage());
    }

    @ParameterizedTest(name = "{0} é par")
    @ValueSource(ints = { 0, 2, 4, 100, -8 })
    @DisplayName("Deve identificar números pares")
    void deveIdentificarNumerosPares(int numero) {
        Assertions.assertTrue(calculadora.ehPar(numero));
    }

    @ParameterizedTest(name = "{0} é ímpar")
    @ValueSource(ints = { 1, 3, 7, 99, -5 })
    @DisplayName("Deve identificar números ímpares")
    void deveIdentificarNumerosImpares(int numero) {
        Assertions.assertFalse(calculadora.ehPar(numero));
    }
}
