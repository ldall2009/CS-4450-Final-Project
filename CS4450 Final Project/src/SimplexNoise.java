
import java.util.Random;


public class SimplexNoise {

    //won't work yet because we may need an octave object for input coordinates
    double[] octaves;
    double[] frequencies;
    double[] amplitudes;
    private int largestFeature;
    private double persistence;
    private int seed;
    private Random r;
    
    public SimplexNoise(int largestFeature, double persistence, int seed){
        this.largestFeature = largestFeature;
        
        octaves = new double[largestFeature];
        frequencies = new double[largestFeature];
        amplitudes = new double[largestFeature];
        
        this.persistence = persistence;
        this.seed = seed;
        
        r = new Random(seed);
        
        for(int i = 0; i < largestFeature; i++){
            //initialize the octaves, frequencies, and amplitudes here
            //use r.nextInt()
            //amplitude is based on persistence
            //
        }
    }
    
    public double getNoise(int x, int y){
        double noiseValue = 0;
        for(int i=0; i < octaves.length; i++){ 
            //must work with x and y for each octave
            noiseValue += octaves[i]/frequencies[i]*amplitudes[i];
        }
        
        return noiseValue;
    }

}
