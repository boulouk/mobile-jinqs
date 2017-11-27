timeONs = [10, 20, 30, 40, 50, 60, 70];

%lifetime = 10

success_rates101020 = [0.0633, 0.109, 0.155, 0.1994, 0.235, 0.276, 0.312];
success_rates101515 = [0.094, 0.165, 0.237, 0.293, 0.357, 0.421, 0.479];
success_rates102010 = [0.126, 0.222, 0.309, 0.393, 0.475, 0.563, 0.636];

a1 = plot(timeONs,success_rates101020);
hold on
a2 = plot(timeONs,success_rates101515);
hold on
a3 = plot(timeONs,success_rates102010);

%lifetime = 30

success_rates301020 = [0.1151, 0.1675, 0.216, 0.2553, 0.278, 0.3092, 0.333];
success_rates301515 = [0.175, 0.247, 0.318, 0.376, 0.422, 0.471, 0.494];
success_rates302010 = [0.238, 0.337, 0.424, 0.505, 0.565, 0.635, 0.664];


a4 = plot(timeONs,success_rates301020);
hold on
a5 = plot(timeONs,success_rates301515);
hold on
a6 = plot(timeONs,success_rates302010);

%lifetime = 60

success_rates601020 = [0.1768, 0.232, 0.265, 0.2882, 0.316, 0.332, 0.335];
success_rates601515 = [0.269, 0.336, 0.399, 0.442, 0.475, 0.491, 0.499];
success_rates602010 = [0.356, 0.459, 0.528, 0.592, 0.636, 0.661, 0.667];

a7 = plot(timeONs,success_rates601020);
hold on
a8 = plot(timeONs,success_rates601515);
hold on
a9 = plot(timeONs,success_rates602010);


M1 = 'Lifetime 10 T\_{ON}^{sub} = 10 T\_{OFF}^{sub} = 20';
M2 = 'Lifetime 10 T\_{ON}^{sub} = 15 T\_{OFF}^{sub} = 15';
M3 = 'Lifetime 10 T\_{ON}^{sub} = 20 T\_{OFF}^{sub} = 10';
M4 = 'Lifetime 30 T\_{ON}^{sub} = 10 T\_{OFF}^{sub} = 20';
M5 = 'Lifetime 30 T\_{ON}^{sub} = 15 T\_{OFF}^{sub} = 15';
M6 = 'Lifetime 30 T\_{ON}^{sub} = 20 T\_{OFF}^{sub} = 10';
M7 = 'Lifetime 60 T\_{ON}^{sub} = 10 T\_{OFF}^{sub} = 20';
M8 = 'Lifetime 60 T\_{ON}^{sub} = 15 T\_{OFF}^{sub} = 15';
M9 = 'Lifetime 60 T\_{ON}^{sub} = 20 T\_{OFF}^{sub} = 10';

legend([a1;a2;a3;a4;a5;a6;a7;a8;a9], M1,M2,M3,M4,M5,M6,M7,M8,M9);