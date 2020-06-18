const express = require("express");
const bodyParser = require("body-parser");

const app = express();

clients = {};

app.use(
    bodyParser.json()
);

app.get("/", function(req, res) {
    res.send("Chat Server API");
});

app.post("/registerClient", function(req, res) {
    console.log("Register client " + req.body.clientId);
    if(!clients[req.body.clientId]) {
        clients[req.body.clientId] = [];
        res.json({
            status: 200,
            message: "Client registered"
        })
    } else {
        res.json({
            status: 200,
            message: "Client already registered"
        })
    }
});

app.post("/deregisterClient", function(req, res) {
    console.log("Deregister client " + req.body.clientId);
    if(!clients[req.body.clientId]) {
        res.json({
            status: 200,
            message: "Client not registered"
        })
    } else {
        delete clients[req.body.clientId]
        res.json({
            status: 200,
            message: "Client de-registered"
        })
    }
});

app.post("/sendMessage", function(req, res) {
    if(!clients[req.body.clientId]) {
        console.log("No client with id " + req.body.clientId)
        res.json({
            status: 200,
            message: "No client with id " + req.body.clientId
        })
    } else {
        console.log("Message received for client " + req.body.clientId);
        console.log("Encrypted message: " + req.body.message);
        clients[req.body.clientId].push({
            message: req.body.message,
            timestamp: req.body.timestamp
        });
        res.json({
            status: 200,
            message: "Message sent"
        })
    }
});

app.get("/getMessages", function(req, res) {
    if(!req.query.clientId) {
        res.json({
            status: 400,
            message: "Bad request"
        })
    }
    if(!clients[req.query.clientId]) {
        res.json({
            status: 401,
            message: "Unauthorized"
        })
    } else {
        if(clients[req.query.clientId].length > 0) {
            res.json({
                status: 200,
                messages: clients[req.query.clientId]
            });
            clients[req.query.clientId] = [];
        } else {
            res.json({
                status: 200,
                messages: []
            });
        }
    }
});

// request to handle undefined or all other routes
app.get("*", function(req, res) {
    res.json({
        status: 404,
        message: "invalid api call"
    });
});

app.listen(3000, function(err) {
    console.log(`Chat Server API listening at http://localhost:${3000}`);
});
