package digitalcity.sim0;

import java.util.*;

public class ParticleMap {

    private int size;

    private List<Particle> listParticles;
    private List<Double> particleWeight;

    public ParticleMap () {
        listParticles = new ArrayList<>();
        particleWeight = new ArrayList<>();
        size = 0;
    }

    /**
     * Add particles and assign the initial weight.
     * @param p Particle added to the map.
     */
    public void add(Particle p) {
        listParticles.add(p);
        particleWeight.add(1.0);
        size += 1;
    }

    /**
     * Compute the effective N used for determining when to resample,
     * which approximately measures the number of particles
     * which meaningfully contribute to the probability distribution.
     * @return effective N.
     */
    public double effectiveN() {
        double sum = 0;
        for (int i = 0; i < particleWeight.size(); i += 1) {
            sum += Math.pow(particleWeight.get(i), 2);
        }
        return 1.0 / sum;
    }

    /**
     * Normalize the list of weights.
     */
    public void normalization() {
        double sum = 0;
        for (int i = 0; i < particleWeight.size(); i++) {
            sum += particleWeight.get(i);
        }
        for (int i = 0; i < particleWeight.size(); i ++) {
            particleWeight.set(i, particleWeight.get(i) / sum);
        }
    }

    /**
     * Resample using indexes.
     */
    public void resampleFromIndex(List<Integer> index) {
        List<Particle> newParticleList = new ArrayList<>();
        List<Double> newWeightList = new ArrayList<>();
        size = index.size();
        for (int i: index) {
            newParticleList.add(get(i));
            newWeightList.add(1.0);
        }
        listParticles = newParticleList;
        particleWeight = newWeightList;
        normalization();
    }

    /**
     * update the weight of particle index.
     * @param index the index of particle
     * @param weight the new weight of particle
     */
    public void updateWeight(int index, double weight) {
        particleWeight.set(index, weight);
        //System.out.println(particleWeight);
    }

    /**
     * Return the index of resampling particles using multinomial resample strategy.
     * @param weights List of particle weights
     * @return index of resampling particles
     */
    public List<Integer> multinomialResample(List<Double> weights) {
        List<Double> cum = new ArrayList<>();
        List<Integer> index = new ArrayList<>();
        Random random = new Random(100);
        double sum = 0;
        cum.add(0.0);
        for (int i = 0; i < weights.size(); i += 1) {
            sum += weights.get(i);
            cum.add(sum);
        }
        cum.set(weights.size() - 1, 1.0);
        for (int i = 0; i < weights.size(); i += 1) {
            double r =random.nextDouble();
            for (int j = 0; j < cum.size(); j += 1) {
                if (r < cum.get(j)) {
                    index.add(j);
                    break;
                }
            }
        }
        return index;
    }

    public void resample() {
        resampleFromIndex(multinomialResample(particleWeight));
    }

    public int size() { return size; }

    public Particle get(int index) { return listParticles.get(index);}

    public List<Particle> getListParticles() { return listParticles; }

    public List<Double> getWeights() { return particleWeight; };

}
