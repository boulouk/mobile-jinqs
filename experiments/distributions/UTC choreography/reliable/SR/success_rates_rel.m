timeONs = [10, 20, 30, 40, 50, 60];

%lifetime = 10

success_rates1060 = [0.0476, 0.232, 0.32, 0.379, 0.464, 0.505];
success_rates1030 = [0.299, 0.43, 0.557, 0.621, 0.668, 0.708];
success_rates1010 = [0.75, 0.85, 0.879, 0.905, 0.931, 0.935];

a1 = plot(timeONs,success_rates1060);
hold on
a2 = plot(timeONs,success_rates1030);
hold on
a3 = plot(timeONs,success_rates1010);

%lifetime = 30

success_rates3060 = [0.0622, 0.304, 0.455, 0.536, 0.581, 0.672];
success_rates3030 = [0.5004, 0.699, 0.728, 0.768, 0.814, 0.839];
success_rates3010 = [0.943, 0.981, 0.979, 0.9857, 0.9856, 0.993];


a4 = plot(timeONs,success_rates3060);
hold on
a5 = plot(timeONs,success_rates3030);
hold on
a6 = plot(timeONs,success_rates3010);

%lifetime = 60

success_rates6060 = [0.143, 0.459, 0.641, 0.669, 0.735, 0.745];
success_rates6030 = [0.747, 0.832, 0.885, 0.915, 0.935, 0.943];
success_rates6010 = [0.995, 0.998, 0.9968, 0.99745, 0.99942, 0.9999598];

a7 = plot(timeONs,success_rates6060);
hold on
a8 = plot(timeONs,success_rates6030);
hold on
a9 = plot(timeONs,success_rates6010);


M1 = 'Lifetime 10 T\_OFF = 60';
M2 = 'Lifetime 10 T\_OFF = 30';
M3 = 'Lifetime 10 T\_OFF = 10';
M4 = 'Lifetime 30 T\_OFF = 60';
M5 = 'Lifetime 30 T\_OFF = 30';
M6 = 'Lifetime 30 T\_OFF = 10';
M7 = 'Lifetime 60 T\_OFF = 60';
M8 = 'Lifetime 60 T\_OFF = 30';
M9 = 'Lifetime 60 T\_OFF = 10';

legend([a1;a2;a3;a4;a5;a6;a7;a8;a9], M1,M2,M3,M4,M5,M6,M7,M8,M9);