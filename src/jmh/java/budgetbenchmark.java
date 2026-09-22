import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

// TODO: Passt den Import an eure echte Klasse an,
// z.B. import ch.bbzbl.budgetplanner.model.BudgetEntry;
// import ch.bbzbl.budgetplanner.service.BudgetService;

@BenchmarkMode(Mode.AverageTime)          // misst durchschnittliche Zeit pro Aufruf
@OutputTimeUnit(TimeUnit.MICROSECONDS)    // Ergebnis in Mikrosekunden
@State(Scope.Thread)                      // jeder Thread bekommt eigenen Zustand
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class BudgetBenchmark {

  // TODO: hier eure echte Service-/Rechenklasse instanziieren
  // z.B. private BudgetService service;
  private List<Double> betraege;

  @Setup(Level.Trial)
  public void setUp() {
    // TODO: hier realistische Testdaten erzeugen,
    // idealerweise in mehreren Grössen testen (siehe Hinweis unten)
    betraege = new ArrayList<>();
    for (int i = 0; i < 1000; i++) {
      betraege.add(Math.random() * 500);
    }
    // service = new BudgetService();
  }

  @Benchmark
  public double testBudgetSummieren() {
    // TODO: ersetzt das durch euren echten Aufruf, z.B.
    // return service.berechneGesamtbudget(betraege);
    double summe = 0;
    for (double betrag : betraege) {
      summe += betrag;
    }
    return summe;
  }

  // Optional: Hauptmethode, falls ihr ohne Maven/Gradle-Plugin
  // direkt aus der IDE starten wollt
  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
            .include(BudgetBenchmark.class.getSimpleName())
            .forks(1)
            .build();
    new Runner(opt).run();
  }
}