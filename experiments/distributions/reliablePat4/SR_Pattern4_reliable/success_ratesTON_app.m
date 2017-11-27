timeONs = [10, 20, 30, 40, 50, 60, 70];

%lifetime = 10

success_rates101020 = [0.089, 0.155, 0.221, 0.278, 0.33, 0.39, 0.44];
success_rates101515 = [0.122, 0.209, 0.307, 0.392, 0.477, 0.561, 0.632];
success_rates102010 = [0.146, 0.265, 0.39, 0.497, 0.596, 0.71,  0.797];

a1 = plot(timeONs,success_rates101020);
hold on
a2 = plot(timeONs,success_rates101515);
hold on
a3 = plot(timeONs,success_rates102010);

%lifetime = 30

success_rates301020 = [0.206, 0.319, 0.422, 0.499, 0.582, 0.659, 0.709];
success_rates301515 = [0.267, 0.394, 0.524, 0.627, 0.721, 0.816, 0.881];
success_rates302010 = [0.314, 0.453, 0.581, 0.701, 0.807, 0.905, 0.963];


a4 = plot(timeONs,success_rates301020);
hold on
a5 = plot(timeONs,success_rates301515);
hold on
a6 = plot(timeONs,success_rates302010);

%lifetime = 60

success_rates601020 = [0.382, 0.519, 0.631, 0.716, 0.795, 0.860, 0.897];
success_rates601515 = [0.453, 0.594, 0.721, 0.817, 0.892, 0.951, 0.977];
success_rates602010 = [0.497, 0.665, 0.764, 0.855, 0.925, 0.979, 0.998];

a7 = plot(timeONs,success_rates601020);
hold on
a8 = plot(timeONs,success_rates601515);
hold on
a9 = plot(timeONs,success_rates602010);


M1 = 'Lifetime 10 T\_ON(mdw) = 10 T\_OFF(mdw) = 20';
M2 = 'Lifetime 10 T\_ON(mdw) = 15 T\_OFF(mdw) = 15';
M3 = 'Lifetime 10 T\_ON(mdw) = 20 T\_OFF(mdw) = 10';
M4 = 'Lifetime 30 T\_ON(mdw) = 10 T\_OFF(mdw) = 20';
M5 = 'Lifetime 30 T\_ON(mdw) = 15 T\_OFF(mdw) = 15';
M6 = 'Lifetime 30 T\_ON(mdw) = 20 T\_OFF(mdw) = 10';
M7 = 'Lifetime 60 T\_ON(mdw) = 10 T\_OFF(mdw) = 20';
M8 = 'Lifetime 60 T\_ON(mdw) = 15 T\_OFF(mdw) = 15';
M9 = 'Lifetime 60 T\_ON(mdw) = 20 T\_OFF(mdw) = 10';

legend([a1;a2;a3;a4;a5;a6;a7;a8;a9], M1,M2,M3,M4,M5,M6,M7,M8,M9);