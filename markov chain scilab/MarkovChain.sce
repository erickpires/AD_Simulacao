function[x] = MarkovChain(lambda, mu, p, n)
    Q = zeros(n,n);
    
    for i = 1:n
        for j = 1:n
            if (i - j == 1) then
                Q(i,j) = mu*(1-p);
            elseif (i - j == -1) then
                Q(i,j) = lambda;
            else
                Q(i,j) = 0;
            end
        end
    end
    
    w = sum(Q, 'c');
    
    for i = 1:n
        for j = 1:n
            if (i == j) then
                Q(i,j) = -w(i);
            end
        end
    end
    
    Qt = Q';
    Qt(n,:) = 1;
    e = zeros(n,1);
    e(n) = 1;
    pi = Qt\e;
    
    t = zeros(1,n);
    
    for i = 1:n
        t(i) = i-1;
    end
    
    x = t*pi;
    
endfunction

function[N] = AutoMarkovChainLambda(a, b, h, n)
    s = (b-a)/h + 1;
    j = 1;
    
    for i = a:h:b
        N(j, 1) = i;
        N(j, 2) = MarkovChain(i, 1, 0, n);
        j = j+1;
    end
    
    write_csv(N, 'scenario1.csv');
    
endfunction

function[N] = AutoMarkovChainMu(a, b, h, n)
    s = (b-a)/h + 1;
    j = 1;
    
    for i = a:h:b
        N(j, 1) = i;
        N(j, 2) = MarkovChain(0.01, i, 0.9, n);
        j = j+1;
    end
    
    write_csv(N, 'scenario4.csv');
    
endfunction
