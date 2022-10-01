
//Denna klass ärver Global så att man kan använda time och signalnamnen utan punktnotation


public class MainSimulation extends Global {

    public static void main(String[] args) {

        //Signallistan startas och actSignal deklareras. actSignal är den senast utplockade signalen i huvudloopen nedan.

        Signal actSignal;

        //Här nedan skapas de processinstanser som behövs och parametrar i dem ges värden.

        var q3 = new QS();
        q3.sendTo = null;
        var q2 = new QS();
        q2.sendTo = q3;
        var q1 = new QS();
        q1.sendTo = q2;

        Gen Generator = new Gen();
        Generator.lambda = 9; //Generator ska generera nio kunder per sekund
        Generator.sendTo = q1; //De genererade kunderna ska skickas till kösystemet QS

        //Här nedan skickas de första signalerna för att simuleringen ska komma igång.

        SignalList.SendSignal(READY, Generator, time);
        SignalList.SendSignal(MEASURE, q1, time);
        SignalList.SendSignal(MEASURE, q2, time);
        SignalList.SendSignal(MEASURE, q3, time);


        // Detta är simuleringsloopen:

        while (time < 100000) {
            actSignal = SignalList.FetchSignal();
            time = actSignal.arrivalTime;
            actSignal.destination.TreatSignal(actSignal);
        }

        //Slutligen skrivs resultatet av simuleringen ut nedan:

        System.out.println("Medelantal kunder i kösystem 1: " + 1.0 * q1.accumulated / q1.noMeasurements);
        System.out.println("Medelantal kunder i kösystem 2: " + 1.0 * q2.accumulated / q2.noMeasurements);
        System.out.println("Medelantal kunder i kösystem 3: " + 1.0 * q3.accumulated / q3.noMeasurements);

    }
}