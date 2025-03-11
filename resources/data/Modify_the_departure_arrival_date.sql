ALTER TABLE t_train_station_relation ADD COLUMN start_time TIME,ADD COLUMN end_time TIME;
UPDATE t_train_station_relation SET start_time = TIME(departure_time),end_time = TIME(arrival_time);