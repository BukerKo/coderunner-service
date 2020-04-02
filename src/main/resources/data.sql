INSERT IGNORE INTO feature (id, feature_name, display_name, enabled)
VALUES (1, 'gatherInformation', 'Gather information', 1);

INSERT IGNORE INTO role (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT IGNORE INTO role (id, name) VALUES (2, 'ROLE_USER');

INSERT IGNORE INTO task (id, task) VALUES (1, 'Print out "Hello, World!"');