timeONs = [10, 20, 30, 40, 50, 60];

success_rates60 = [0.138, 0.253, 0.357, 0.416, 0.445, 0.481];
success_rates30 = [0.239, 0.396, 0.498, 0.549, 0.5832, 0.643];
success_rates10 = [0.506, 0.671, 0.758, 0.808, 0.824, 0.852];

a1 = plot(timeONs,success_rates60);
hold on
a2 = plot(timeONs,success_rates30);
hold on
a3 = plot(timeONs,success_rates10);



M1 = 'T\_OFF = 60';
M2 = 'T\_OFF = 30';
M3 = 'T\_OFF = 10';


legend([a1;a2;a3], M1,M2,M3);