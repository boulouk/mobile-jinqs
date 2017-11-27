timeouts40 = [10, 15, 20, 25, 30];
timeouts50 = [10, 15, 20, 25, 30, 35, 40];
timeouts60 = [10, 15, 20, 25, 30, 35, 40, 45, 50];

%lifetime = 10

success_rates1040 = [0.224, 0.443, 0.605, 0.751, 0.884];
success_rates1050 = [0.106, 0.281, 0.435, 0.579, 0.692, 0.80, 0.908];
success_rates1060 = [0.033,  0.194, 0.323, 0.436, 0.547, 0.654, 0.747, 0.837, 0.923];

a1 = plot(timeouts40,success_rates1040);
hold on
a2 = plot(timeouts50,success_rates1050);
hold on
a3 = plot(timeouts60,success_rates1060);

%lifetime = 20

success_rates2040 = [0.333, 0.576, 0.733, 0.865, 0.955];
success_rates2050 = [0.163, 0.391, 0.554, 0.694, 0.804, 0.897, 0.964];
success_rates2060 = [0.049, 0.262, 0.413, 0.533, 0.662, 0.757, 0.837, 0.913, 0.972];

a4 = plot(timeouts40,success_rates2040);
hold on
a5 = plot(timeouts50,success_rates2050);
hold on
a6 = plot(timeouts60,success_rates2060);

%lifetime = 30

success_rates3040 = [0.419, 0.681, 0.824, 0.921, 0.984];
success_rates3050 = [0.209, 0.479, 0.645, 0.775, 0.872, 0.943, 0.986];
success_rates3060 = [0.065, 0.343, 0.515, 0.618, 0.727, 0.828, 0.901, 0.954, 0.989];

a7 = plot(timeouts40,success_rates3040);
hold on
a8 = plot(timeouts50,success_rates3050);
hold on
a9 = plot(timeouts60,success_rates3060);


M1 = 'Lifetime 10 d_get = 40';
M2 = 'Lifetime 10 d_get = 50';
M3 = 'Lifetime 10 d_get = 60';
M4 = 'Lifetime 20 d_get = 40';
M5 = 'Lifetime 20 d_get = 50';
M6 = 'Lifetime 20 d_get = 60';
M7 = 'Lifetime 30 d_get = 40';
M8 = 'Lifetime 30 d_get = 50';
M9 = 'Lifetime 30 d_get = 60';

legend([a1;a2;a3;a4;a5;a6;a7;a8;a9], M1,M2,M3,M4,M5,M6,M7,M8,M9);