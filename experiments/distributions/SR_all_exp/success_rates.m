lifetimes = [10, 20, 40];

% timeout 10

success_rates10 = [0.6094, 0.712, 0.8245];

% timeout 20

success_rates20 = [0.4814, 0.581, 0.7213];

% timeout 40

success_rates40 = [0.405, 0.484, 0.5837];

a1 = plot(lifetimes,success_rates10);

hold on

a2 = plot(lifetimes,success_rates20);

hold on

a3 = plot(lifetimes,success_rates40);


M1 = 'Lifetime 10';
M2 = 'Lifetime 20';
M3 = 'Lifetime 40';

legend([a1;a2;a3], M1,M2,M3);