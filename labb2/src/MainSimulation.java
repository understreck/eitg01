
//Denna klass ärver Global så att man kan använda time och signalnamnen utan punktnotation


public class MainSimulation extends Global {

    public static void main(String[] args) {

        //Signallistan startas och actSignal deklareras. actSignal är den senast utplockade signalen i huvudloopen nedan.

        Signal actSignal;

        //Här nedan skapas de processinstanser som behövs och parametrar i dem ges värden.


        var dispatchers = new Dispatcher[]{new Dispatcher(
                new QS[]{new QS(), new QS(), new QS(), new QS(), new QS()},
                Dispatcher.Strategy.RANDOM),
                new Dispatcher(
                        new QS[]{new QS(), new QS(), new QS(), new QS(), new QS()},
                        Dispatcher.Strategy.SEQUENTIAL),
                new Dispatcher(
                        new QS[]{new QS(), new QS(), new QS(), new QS(), new QS()},
                        Dispatcher.Strategy.LEAST_WORK)};

        var gens = new Gen[dispatchers.length];
        for(int i = 0; i < dispatchers.length; ++i) {
            gens[i] = new Gen();
            gens[i].lambda = 45; //Generator ska generera nio kunder per sekund
            gens[i].sendTo = dispatchers[i]; //De genererade kunderna ska skickas till kösystemet QS
        }

        //Här nedan skickas de första signalerna för att simuleringen ska komma igång.

        for(var g : gens) {
            SignalList.SendSignal(READY, g, time);
        }

        // Detta är simuleringsloopen:

        while (time < 100000) {
            actSignal = SignalList.FetchSignal();
            time = actSignal.arrivalTime;
            actSignal.destination.TreatSignal(actSignal);
        }

        for(var d : dispatchers) {
            d.print_diags();
        }
    }
}