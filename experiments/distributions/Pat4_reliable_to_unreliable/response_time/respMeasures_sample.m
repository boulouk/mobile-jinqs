[f1,x1]=ecdf(responses1030501020);


npoints = 80;
interval = round(length(x1)/npoints);
a1 = plot(x1(1:interval:end),f1(1:interval:end))

%a1 = plot(x1,f1);

M1 = 'Curve 1030501020';

legend([a1], M1);