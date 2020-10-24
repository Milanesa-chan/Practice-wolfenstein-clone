public class FPSCounter implements Runnable{
    public int framesHastaAhora = 0;
    public double fps = 0;

    public FPSCounter(){
        new Thread(this).start();
    }
    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(500);
                fps = framesHastaAhora*2;
                framesHastaAhora = 0;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
