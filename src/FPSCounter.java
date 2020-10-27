import java.util.ArrayList;

public class FPSCounter implements Runnable{
    private static FPSCounter instance;
    public int framesHastaAhora = 0;
    public double fps = 0;
    private final ArrayList<Integer> pingResults;
    public int ping = 0;
    private FPSCounter(){
        pingResults = new ArrayList<>();
        new Thread(this).start();
    }

    public static FPSCounter getInstance(){
        if(instance == null)
            instance = new FPSCounter();
        return instance;
    }

    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(500);
                fps = framesHastaAhora*2;
                int pingSuma = 0;
                synchronized (pingResults) {
                    for (int i : pingResults) {
                        pingSuma += i;
                    }
                    ping = pingResults.size()==0 ? 0 : pingSuma/pingResults.size();
                    pingResults.clear();
                }
                framesHastaAhora = 0;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void agregarPing(int ping){
        synchronized (pingResults) {
            pingResults.add(ping);
        }
    }

}
