import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Dispatcher extends Proc {
    private final QS[] m_queues;
    private final Strategy m_strategy;

    private final Random m_rand = new Random();
    private int m_currentQueue = 0;

    public enum Strategy {
        RANDOM,
        SEQUENTIAL,
        LEAST_WORK
    }

    Dispatcher(QS[] queues, Strategy strategy) {
        m_queues = queues;

        for (var q : m_queues) {
            SignalList.SendSignal(MEASURE, q, time);
        }

        m_strategy = strategy;
    }

    private int rand_qs() {
        final var bound = m_queues.length;

        return m_rand.nextInt(bound);
    }

    private int next_q() {
        final var bound = m_queues.length;
        if (++m_currentQueue >= bound) m_currentQueue = 0;

        return m_currentQueue;
    }

    public void TreatSignal(Signal x) {
        switch (m_strategy) {
            case RANDOM -> m_queues[rand_qs()].TreatSignal(x);
            case SEQUENTIAL -> m_queues[next_q()].TreatSignal(x);
            case LEAST_WORK ->
                    Arrays.stream(m_queues).min(Comparator.comparingInt(q -> q.numberInQueue)).orElseThrow().TreatSignal(x);
        }
    }

    public void print_diags() {
        switch (m_strategy) {
            case RANDOM -> System.out.println("\nStrategy of RANDOM:");
            case SEQUENTIAL -> System.out.println("\nStrategy of SEQUENTIAL:");
            case LEAST_WORK -> System.out.println("\nStrategy of LEAST_WORK:");
        }

        int i = 0;
        for (var q : m_queues) {
            System.out.printf("Medelantal kunder i kösystem %d: %f\n", i++, 1.0 * q.accumulated / q.noMeasurements);
        }
    }
}
