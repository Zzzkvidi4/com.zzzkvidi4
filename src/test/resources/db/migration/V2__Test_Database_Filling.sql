insert into item (item_id, name, code) VALUES ('1', 'Sphere', '001');
insert into item (item_id, name, code) VALUES ('2', 'Box', '002');
insert into item (item_id, name, code) VALUES ('3', 'Cylinder', '003');
insert into item (item_id, name, code) VALUES ('4', 'Cube', '004');
insert into item (item_id, name, code) VALUES ('5', 'Hectopod', '005');
insert into item (item_id, name, code) VALUES ('6', 'Triangle', '006');
insert into item (item_id, name, code) VALUES ('7', 'Square', '007');

insert into organization (organization_id, name, itn, account) VALUES ('1', 'ibm', '111111111', '111');
insert into organization (organization_id, name, itn, account) VALUES ('2', 'intel', '222222222', '222');
insert into organization (organization_id, name, itn, account) VALUES ('3', 'amd', '333333333', '333');
insert into organization (organization_id, name, itn, account) VALUES ('4', 'skynet', '4444444444', '444');
insert into organization (organization_id, name, itn, account) VALUES ('5', 'skyhawk', '5555555555', '555');
insert into organization (organization_id, name, itn, account) VALUES ('6', 'dataart', '6666666666', '666');
insert into organization (organization_id, name, itn, account) VALUES ('7', 'dsr', '7777777777', '777');
insert into organization (organization_id, name, itn, account) VALUES ('8', 'atos', '88888888888', '888');
insert into organization (organization_id, name, itn, account) VALUES ('9', 'jetbrains', '9999999999', '999');
insert into organization (organization_id, name, itn, account) VALUES ('10', 'huawei', '0000000000', '000');
insert into organization (organization_id, name, itn, account) VALUES ('11', 'argus', '0000000001', '001');
insert into organization (organization_id, name, itn, account) VALUES ('12', 'green arrow', '0000000002', '002');
insert into organization (organization_id, name, itn, account) VALUES ('13', 'flash', '0000000003', '003');


insert into invoice (invoice_id, date, organization_id) VALUES ('1', '2019-01-02 14:56:22.210000', '1');
insert into invoice (invoice_id, date, organization_id) VALUES ('2', '2019-08-02 14:56:22.210000', '2');
insert into invoice (invoice_id, date, organization_id) VALUES ('3', '2019-08-03 14:56:22.210000', '3');
insert into invoice (invoice_id, date, organization_id) VALUES ('4', '2019-08-04 14:56:22.210000', '4');
insert into invoice (invoice_id, date, organization_id) VALUES ('5', '2019-08-05 14:56:22.210000', '5');
insert into invoice (invoice_id, date, organization_id) VALUES ('6', '2019-08-06 14:56:22.210000', '6');
insert into invoice (invoice_id, date, organization_id) VALUES ('13', '2019-08-06 14:56:22.210000', '5');
insert into invoice (invoice_id, date, organization_id) VALUES ('7', '2019-08-10 14:56:22.210000', '7');
insert into invoice (invoice_id, date, organization_id) VALUES ('8', '2019-08-12 14:56:22.210000', '8');
insert into invoice (invoice_id, date, organization_id) VALUES ('9', '2019-08-13 14:56:22.210000', '9');
insert into invoice (invoice_id, date, organization_id) VALUES ('10', '2019-02-02 14:56:22.210000', '10');
insert into invoice (invoice_id, date, organization_id) VALUES ('11', '2019-04-02 14:56:22.210000', '11');
insert into invoice (invoice_id, date, organization_id) VALUES ('12', '2019-05-02 14:56:22.210000', '12');

insert into invoice_item (invoice_item_id, item_id, invoice_id, price, volume)
VALUES ('1', '1', '1', 100, 20);
insert into invoice_item (invoice_item_id, item_id, invoice_id, price, volume)
VALUES ('2', '2', '2', 150, 70);
insert into invoice_item (invoice_item_id, item_id, invoice_id, price, volume)
VALUES ('3', '3', '3', 1, 1500);
insert into invoice_item (invoice_item_id, item_id, invoice_id, price, volume)
VALUES ('4', '4', '4', 2, 60);
insert into invoice_item (invoice_item_id, item_id, invoice_id, price, volume)
VALUES ('5', '5', '5', 3, 20);
insert into invoice_item (invoice_item_id, item_id, invoice_id, price, volume)
VALUES ('6', '6', '6', 17, 840);
insert into invoice_item (invoice_item_id, item_id, invoice_id, price, volume)
VALUES ('7', '7', '7', 123, 2);
insert into invoice_item (invoice_item_id, item_id, invoice_id, price, volume)
VALUES ('8', '6', '8', 61, 3);
insert into invoice_item (invoice_item_id, item_id, invoice_id, price, volume)
VALUES ('9', '5', '9', 74, 7);
insert into invoice_item (invoice_item_id, item_id, invoice_id, price, volume)
VALUES ('10', '2', '1', 1000, 5);
insert into invoice_item (invoice_item_id, item_id, invoice_id, price, volume)
VALUES ('11', '3', '2', 100, 20);
insert into invoice_item (invoice_item_id, item_id, invoice_id, price, volume)
VALUES ('12', '7', '13', 100, 200);