fact('Papua New Guinea',44876).
fact('Cambodia',137538).
fact('Kazakhstan',1476926).
fact('Paraguay',715162).
fact('Syria',56973).
fact('Bahamas',37044).
fact('Solomon Islands',21544).
fact('Mali',31314).
fact('Marshall Islands',14506).
fact('Panama',972800).
fact('Laos',213738).
fact('Argentina',9658391).
fact('Seychelles',45852).
fact('Belize',68187).
fact('Zambia',332710).
fact('Bahrain',670854).
fact('Guinea-Bissau',8482).
fact('Namibia',169253).
fact('Comoros',8445).
fact('Finland',1258798).
fact('North Macedonia',339492).
fact('Georgia',1735682).
fact('Saint Kitts and Nevis',6509).
fact('Yemen',11925).
fact('Eritrea',10151).
fact('Winter Olympics 2022',535).
fact('Madagascar',66615).
fact('Libya',506746).
fact('Sweden',2564423).
fact('Taiwan*',5194850).
fact('Malawi',87834).
fact('Andorra',46027).
fact('Liechtenstein',19086).
fact('Poland',6166596).
fact('Bulgaria',1239681).
fact('Jordan',1731549).
fact('Tunisia',1143167).
fact('United Arab Emirates',1013331).
fact('Kenya',338153).
fact('Brunei',220245).
fact('Djibouti',15690).
fact('Lebanon',1207873).
fact('Azerbaijan',811559).
fact('Cuba',1110337).
fact('Mauritania',62759).
fact('Saint Lucia',28689).
fact('Israel',4629050).
fact('San Marino',20323).
fact('Australia',9997168).
fact('Tajikistan',17786).
fact('Cameroon',121652).
fact('Cyprus',576278).
fact('Malaysia',4774003).
fact('Iceland',204268).
fact('Oman',397846).
fact('MS Zaandam',9).
fact('Armenia',432274).
fact('Gabon',48635).
fact('Luxembourg',286289).
fact('Brazil',34368909).
fact('Algeria',270175).
fact('Cabo Verde',62309).
fact('Slovenia',1121965).
fact('Diamond Princess',712).
fact('US',94184146).
fact('Antigua and Barbuda',8949).
fact('Colombia',6299595).
fact('Ecuador',993858).
fact('Moldova',569088).
fact('Korea North',1).
fact('Vanuatu',11784).
fact('Eswatini',73358).
fact('Honduras',452305).
fact('Italy',21788862).
fact('Antarctica',11).
fact('Summer Olympics 2020',865).
fact('Haiti',33295).
fact('Afghanistan',191967).
fact('Burundi',49063).
fact('Singapore',1831332).
fact('Russia',19077368).
fact('Netherlands',8490917).
fact('Cote dIvoire',86606).
fact('China',2452580).
fact('Kyrgyzstan',205537).
fact('Bhutan',60898).
fact('Romania',3208903).
fact('Togo',38419).
fact('Philippines',3872405).
fact('Congo (Kinshasa)',92634).
fact('Uzbekistan',243743).
fact('Zimbabwe',256682).
fact('Montenegro',274744).
fact('Dominica',14852).
fact('Indonesia',6343076).
fact('Benin',27316).
fact('Angola',102636).
fact('Sudan',63173).
fact('Portugal',5409185).
fact('Grenada',19268).
fact('Greece',4712025).
fact('Latvia',895513).
fact('Mongolia',976751).
fact('Iran',7521969).
fact('Morocco',1264286).
fact('Guatemala',1096530).
fact('Guyana',70966).
fact('Iraq',2456555).
fact('Chile',4487453).
fact('Nepal',996670).
fact('Tanzania',38712).
fact('Ukraine',5326639).
fact('Ghana',168565).
fact('Congo (Brazzaville)',24837).
fact('Holy See',29).
fact('India',44408132).
fact('Canada',4187887).
fact('Maldives',184788).
fact('Turkey',16671848).
fact('Belgium',4477770).
fact('South Africa',4011210).
fact('Trinidad and Tobago',178532).
fact('Central African Republic',14862).
fact('Jamaica',149420).
fact('Peru',4097530).
fact('Germany',32041349).
fact('Fiji',68116).
fact('Guinea',37470).
fact('Chad',7491).
fact('Somalia',27020).
fact('Sao Tome and Principe',6136).
fact('Thailand',4646412).
fact('Equatorial Guinea',16945).
fact('Kiribati',3430).
fact('Costa Rica',1079825).
fact('Korea South',22983818).
fact('Vietnam',11401597).
fact('West Bank and Gaza',698384).
fact('Kuwait',657042).
fact('Nigeria',263407).
fact('Croatia',1211419).
fact('Sri Lanka',669693).
fact('Uruguay',977305).
fact('Timor-Leste',23152).
fact('United Kingdom',23708629).
fact('Switzerland',4029657).
fact('Samoa',15767).
fact('Spain',13332976).
fact('Liberia',7732).
fact('Venezuela',542199).
fact('Burkina Faso',21128).
fact('Palau',5348).
fact('Estonia',596763).
fact('Austria',4934634).
fact('Mozambique',230064).
fact('El Salvador',190818).
fact('Monaco',14363).
fact('Lesotho',34206).
fact('Tonga',15235).
fact('Hungary',2036390).
fact('South Sudan',17823).
fact('Japan',18374230).
fact('Belarus',994037).
fact('Mauritius',254400).
fact('Albania',328299).
fact('New Zealand',1740422).
fact('Senegal',88024).
fact('Ethiopia',493142).
fact('Czechia',4035632).
fact('Egypt',515645).
fact('Sierra Leone',7746).
fact('Bolivia',1099718).
fact('Malta',113972).
fact('Saudi Arabia',813107).
fact('Pakistan',1568453).
fact('Kosovo',271063).
fact('Gambia',12311).
fact('Ireland',1655338).
fact('Qatar',427001).
fact('Slovakia',2584276).
fact('France',34662834).
fact('Lithuania',1215461).
fact('Serbia',2269421).
fact('Bosnia and Herzegovina',395086).
fact('Niger',9263).
fact('Rwanda',132414).
fact('Burma',614573).
fact('Bangladesh',2011100).
fact('Barbados',100676).
fact('Nicaragua',14931).
fact('Norway',1459704).
fact('Botswana',325864).
fact('Saint Vincent and the Grenadines',9447).
fact('Denmark',3315957).
fact('Dominican Republic',637573).
fact('Mexico',7001590).
fact('Uganda',169396).
fact('Micronesia',7727).
fact('Suriname',81022).

%sort ascending
quick_sort_asc([],[]).
quick_sort_asc([H|T], Sorted) :-
    partition(H,T,L,G),
    quick_sort_asc(L, SortedL),
    quick_sort_asc(G, SortedG),
    append(SortedL,[H|SortedG],Sorted).

partition(_,[],[],[]).
partition(P,[H|T],[H|L],G) :-
    H =< P,
    partition(P,T,L,G).
partition(P,[H|T],L,[H|G]) :-
    H > P,
    partition(P,T,L,G).

%sort descending
msort_desc([],[]).
msort_desc(List, Sorted) :- sort(0, @>=, List, Sorted).