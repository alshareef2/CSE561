function dist = vanilla(A, B)
    dist = 0;
    for i = 1:size(A,1)
        dist = dist + (A(i) - B(i)).^2;
    end
end