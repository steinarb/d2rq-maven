CREATE TABLE TABLE1 (ID INT PRIMARY KEY, REF1 INT, REF2 INT);
INSERT INTO TABLE1 VALUES (1, 10, 1);
INSERT INTO TABLE1 VALUES (2, 10, 2);
INSERT INTO TABLE1 VALUES (3, 11, 1);
INSERT INTO TABLE1 VALUES (4, NULL, NULL);
CREATE TABLE TABLE2 (ID1 INT, ID2 INT, PRIMARY KEY (ID1, ID2));
INSERT INTO TABLE2 VALUES (10, 1);
INSERT INTO TABLE2 VALUES (10, 2);
INSERT INTO TABLE2 VALUES (11, 1);
INSERT INTO TABLE2 VALUES (12, 1);
