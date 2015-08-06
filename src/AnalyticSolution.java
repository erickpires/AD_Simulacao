/**
 * Created by vitortrindade on 06/08/15.
 * Levou mais tempo que eu esperava
 */
public class AnalyticSolution {

    public double lambda, mu,p,N,rho;

    public AnalyticSolution(double lambda,double mu, double p) {

        this.lambda = lambda;
        this.mu = mu;
        this.p = p;


    }

    public double getMeanNumber() {
        rho = lambda / ( mu * ( 1-p ));

        N = rho / (1 - rho);

        return N;
    }
    

}
