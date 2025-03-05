/*
 Navicat Premium Data Transfer

 Source Server         : MONGO_LOCAL
 Source Server Type    : MongoDB
 Source Server Version : 80004 (8.0.4)
 Source Host           : localhost:27017
 Source Schema         : microservice_credit_service

 Target Server Type    : MongoDB
 Target Server Version : 80004 (8.0.4)
 File Encoding         : 65001

 Date: 05/03/2025 01:46:36
*/


// ----------------------------
// Collection structure for credits
// ----------------------------
db.getCollection("credits").drop();
db.createCollection("credits");

// ----------------------------
// Documents of credits
// ----------------------------
db.getCollection("credits").insert([ {
    _id: ObjectId("67bf8fefddc2c72eb4dad76d"),
    customerId: "67be562286d43e7cd15520ce",
    creditType: "PERSONAL",
    creditLimit: "1000",
    availableCredit: "1000",
    currency: "PEN",
    _class: "com.skoy.microservice_credit_service.model.Credit"
} ]);
db.getCollection("credits").insert([ {
    _id: ObjectId("67c2365cbcad3f7a327db0e4"),
    customerId: "67bc5049652bf702f7552f25",
    creditType: "PERSONAL",
    creditLimit: "1000",
    availableBalance: "900",
    usedCredit: "100",
    currency: "PEN",
    _class: "com.skoy.microservice_credit_service.model.Credit"
} ]);
db.getCollection("credits").insert([ {
    _id: ObjectId("67c247b91211af142e37d120"),
    customerId: "67bc5049652bf702f7552f25",
    creditType: "TARJETA_CREDITO",
    creditLimit: "1000",
    availableBalance: "1000",
    currency: "PEN",
    _class: "com.skoy.microservice_credit_service.model.Credit"
} ]);
