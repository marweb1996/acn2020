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
    console.log("heyho");
    // clientId
    // message
    if(!clients[req.body.clientId]) {
        clients[req.body.clientId] = [];
    }
    clients[req.body.clientId].push(req.body.message);
});

app.get("/getMessages", function(req, res) {
    if(!req.query.clientId) {
        res.send("Bad request");
    }
    if(!clients[req.query.clientId]) {
        clients[req.query.clientId] = [];
    }
    if(clients[req.query.clientId].length > 0) {
        req.json(clients[req.query.clientId]);
        clients[req.query.clientId] = [];
    } else {
        res.send("No new messages");
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
