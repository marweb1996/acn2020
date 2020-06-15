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

app.post("/sendMessage", function(req, res) {
    console.log("Message received for client " + req.body.clientId);
    console.log("Encrypted message: " + req.body.message);
    if(!clients[req.body.clientId]) {
        clients[req.body.clientId] = [];
    }
    clients[req.body.clientId].push({
        message: req.body.message,
        timestamp: req.body.timestamp
    });
    res.json({
        status: 200,
        message: "Message sent"
    })
});

app.get("/getMessages", function(req, res) {
    if(!req.query.clientId) {
        res.send("Bad request");
    }
    if(!clients[req.query.clientId]) {
        clients[req.query.clientId] = [];
    }
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
