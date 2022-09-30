import java.util.Random;

class State extends GlobalSimulation {

    public int numberAsInQueue = 0, numberBsInQueue, accumulated = 0, noMeasurements = 0;
    public boolean AInFlight = false;

    private EventList myEventList;

    Random slump = new Random();

    State(EventList x) {
        myEventList = x;
    }

    private void InsertEvent(int event, double timeOfEvent) {
        myEventList.InsertEvent(event, timeOfEvent);
    }


    public void TreatEvent(Event x) {
        switch (x.eventType) {
            case ARRIVAL_A: {
                handle_ARRIVAL_A("It was the next available event");
                break;
            }
            case READY_A: {
                handle_READY_A("It was the next available event");
                break;
            }
            case ARRIVAL_B: {
                handle_ARRIVAL_B("It was the next available event");
                break;
            }
            case READY_B: {
                handle_READY_B("It was the next available event");
                break;
            }
            case MEASURE: {
                measure();
                break;
            }
        }
    }

    private double generateMean(double mean) {
        return 2 * mean * slump.nextDouble();
    }

    private void add_READY_A(String why) {
        var delay = 0.002;
        if (debug) {
            System.out.println("Adding a READY_A event " + delay + "s from now because:\n" + why);
            if (AInFlight) {
                System.out.println(
                        "Another READY_A is already on flight, this should never happen.\n" +
                                "I was called because: " + why);
                System.exit(-1);
            }
        }

        AInFlight = true;
        InsertEvent(READY_A, time + delay);
    }

    private void add_READY_B(String why) {
        var delay = 0.004;
        if (debug) {
            System.out.println("Adding a READY_B event " + delay + "s from now because:\n" + why);
        }

        InsertEvent(READY_B, time + delay);
    }

    private void add_ARRIVAL_A(String why) {
        final var lambda = 150.0;
//        final var meanTimeOffset = (2.0 / 150.0) / 2.0;

        var delay = generateMean(1.0 / 150.0);
        if (debug) {
            System.out.println("Adding an ARRIVAL_A event " + delay + "s from now because:\n" + why);
        }

        InsertEvent(ARRIVAL_A, time + delay);
    }

    private void add_ARRIVAL_B(String why) {
        final var lambda = 1.0;

        var delay = 1.0 / lambda;
        if (debug) {
            System.out.println("Adding an ARRIVAL_B event " + delay + "s from now because:\n" + why);
        }

        InsertEvent(ARRIVAL_B, time + delay);
    }

    private void handle_ARRIVAL_A(String why) {
        if (debug) {
            System.out.println("Handling ARRIVAL_A event because:\n" + why);
        }
        add_ARRIVAL_A("There should always be a next ARRIVAL_A");

        if (numberBsInQueue == 0 && numberAsInQueue == 0) {
            add_READY_A(
                    "There are " + numberAsInQueue + " A's in queue AND " + numberBsInQueue + " B's in queue.\n" +
                            "There should only ever be one READY_* event in flight at a time");
        }

        numberAsInQueue++;
    }

    private void handle_ARRIVAL_B(String why) {
        if (debug) {
            System.out.println("Handling ARRIVAL_B event because:\n" + why);
        }

        if (AInFlight != true && numberBsInQueue == 0) {
            add_READY_B(
                    "There are " + numberAsInQueue + " A's in queue AND " + numberBsInQueue + " B's in queue.\n" +
                            "There should only ever be one READY_* event in flight at a time, though B's take priority");
        }

        numberBsInQueue++;
    }

    private void handle_READY_A(String why) {
        if (debug) {
            System.out.println("Handling READY_A event because:\n" + why);
            if (AInFlight == false) {
                System.out.println(
                        "No READY_A is in flight, this should never happen.\n" +
                                "I was called because: " + why);
                System.exit(-1);
            }
        }

        AInFlight = false;
        numberAsInQueue--;

        add_ARRIVAL_B("After every A is finished, a B should be sent");

        if (numberBsInQueue == 0 && numberAsInQueue > 0) {
            add_READY_A(
                    "There are " + numberAsInQueue + " A's in queue AND " + numberBsInQueue + " B's in queue.\n" +
                            "There should only ever be one READY_* event in flight at a time");
        } else if (numberBsInQueue > 0) {
            add_READY_B(
                    "There are " + numberAsInQueue + " A's in queue AND " + numberBsInQueue + " B's in queue.\n" +
                            "There should only ever be one READY_* event in flight at a time, though B's take priority");
        }
    }

    private void handle_READY_B(String why) {
        if (debug) {
            System.out.println("Handling READY_B event because:\n" + why);
            if (AInFlight == true) {
                System.out.println(
                        "A READY_A is in flight, this should never happen.\n" +
                                "I was called because: " + why);
                System.exit(-1);
            }
        }

        numberBsInQueue--;

        if (numberBsInQueue > 0) {
            add_READY_B(
                    "There are " + numberAsInQueue + " A's in queue AND " + numberBsInQueue + " B's in queue.\n" +
                            "There should only ever be one READY_* event in flight at a time, though B's take priority");
        } else if (numberAsInQueue > 0) {
            add_READY_A(
                    "There are " + numberAsInQueue + " A's in queue AND " + numberBsInQueue + " B's in queue.\n" +
                            "There should only ever be one READY_* event in flight at a time");
        }
    }

    private void measure() {
        accumulated = accumulated + numberAsInQueue + numberBsInQueue;
        noMeasurements++;
        InsertEvent(MEASURE, time + 0.1);
    }
}