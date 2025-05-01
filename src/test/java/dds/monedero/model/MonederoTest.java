package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MonederoTest {
  private Cuenta cuenta;

  @BeforeEach
  void init() {
    cuenta = new Cuenta(0);
  }

  @Test
  @DisplayName("Es posible poner $1500 en una cuenta vacía")
  void Poner() {
    cuenta.poner(1500);
    assertEquals(1500, cuenta.getSaldo());
  }

  @Test
  @DisplayName("No es posible poner montos negativos")
  void PonerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.poner(-1500));
  }

  @Test
  @DisplayName("Es posible realizar múltiples depósitos consecutivos")
  void TresDepositos() {
    cuenta.poner(1500);
    cuenta.poner(456);
    cuenta.poner(1900);
  }

  @Test
  @DisplayName("No es posible superar la máxima cantidad de depositos diarios")
  void MasDeTresDepositos() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
      cuenta.poner(1500);
      cuenta.poner(456);
      cuenta.poner(1900);
      cuenta.poner(245);
    });
  }

  @Test
  @DisplayName("No es posible extraer más que el saldo disponible")
  void ExtraerMasQueElSaldo() {
    assertThrows(SaldoMenorException.class, () -> {
      cuenta = new Cuenta(90);
      cuenta.sacar(800);
    });
  }

  @Test
  @DisplayName("No es posible extraer más que el límite diario")
  void ExtraerMasDe1000() {
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta = new Cuenta(5000);
      cuenta.sacar(1001);
    });
  }

  @Test
  @DisplayName("No es posible extraer un monto negativo")
  void ExtraerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.sacar(-500));
  }

  @Test
  @DisplayName("Extraer dinero correctamente actualiza el saldo")
  void ExtraerCorrectamente() {
    cuenta = new Cuenta(2000);
    cuenta.sacar(500);
    assertEquals(1500, cuenta.getSaldo());
  }

  @Test
  @DisplayName("Es posible extraer varias veces en el día sin superar el límite diario")
  void MultiplesExtraccionesValidas() {
    cuenta = new Cuenta(2000);
    cuenta.sacar(400);
    cuenta.sacar(300);
    cuenta.sacar(300);
    assertEquals(1000, cuenta.getSaldo());
  }

  @Test
  @DisplayName("Los movimientos se registran en la cuenta")
  void RegistroDeMovimientos() {
    cuenta = new Cuenta(0);
    cuenta.poner(1000);
    cuenta.poner(200);
    cuenta.sacar(500);
    assertEquals(3, cuenta.getMovimientos().size());
  }

  @Test
  @DisplayName("El monto extraído hoy se calcula correctamente")
  void MontoExtraidoHoy() {
    cuenta = new Cuenta(2000);
    cuenta.sacar(200);
    cuenta.sacar(300);
    assertEquals(500, cuenta.getMontoExtraidoA(LocalDate.now()));
  }

  @Test
  @DisplayName("El saldo se actualiza correctamente con varios movimientos")
  void SaldoFinal() {
    cuenta = new Cuenta(1000);
    cuenta.poner(500);
    cuenta.sacar(200);
    cuenta.poner(200);
    cuenta.sacar(100);
    assertEquals(1400, cuenta.getSaldo());
  }


}