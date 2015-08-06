/**
 * Created by vitortrindade on 06/08/15.
 * Levou mais tempo que eu esperava
 */
public class AnalyticSolution {

    public double N,rho;

    public AnalyticSolution() {

    }

    public double getMeanNumber(double lambda,double mu, double p) {
        rho = lambda / ( mu * ( 1-p ));

        N = rho / (1 - rho);

        return N;
    }


}
