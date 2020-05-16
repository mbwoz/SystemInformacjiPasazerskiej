#!/bin/bash

cat clear.sql > create.sql;

cat createTables.sql >> create.sql;

cat utils/sequenceTriggers.sql >> create.sql;
cat utils/queryFunctions.sql >> create.sql;

cat rozklad/header.in >> create.sql;
cat rozklad/trasy.in >> create.sql;
cat rozklad/stacje.in >> create.sql;
cat rozklad/odcinki.in >> create.sql;
cat rozklad/trasy_odcinki.in >> create.sql;
cat rozklad/pociagi.in >> create.sql;
cat rozklad/rozklady.in >> create.sql;
cat rozklad/sklady.in >> create.sql;
cat rozklad/postoje.in >> create.sql;
cat rozklad/wagony.in >> create.sql;
cat rozklad/sklady_wagony.in >> create.sql;
