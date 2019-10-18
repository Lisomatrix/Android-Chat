const express = require('express')
const app = express()
const port = 3000
const server = require('http').Server(app);
const io = require('socket.io')(server);
const shortid = require('shortid')
const bodyParser = require('body-parser')

const Database = require('./database');

const db = new Database();

app.use(bodyParser.urlencoded({ extended: false }))
app.use(bodyParser.json())

app.post('/register', (req, res) => {
    db.setElement('users', {
        userId: shortid(),
        name: req.body.name,
        email: req.body.email,
        password: req.body.password
    });

    res.status(200).end();
});

app.post('/login', (req, res) => {
    const foundUser = db.getElement('users', { email: req.body.email, password: req.body.password });
   
    if (foundUser) {
        const token = {
            token: shortid(),
            userId: foundUser.userId
        };

        db.setElement('tokens', token);

        res.send(token);
    }

    res.status(401).end();
});

app.get('/me', isAuthenticated, (req, res) => {
    const token = req.header('authorization');

    const foundToken = db.getElement('tokens', { token });
    const foundUser = db.getElement('users', { userId: foundToken.userId });

    res.send(foundUser);
});

app.get('/user', isAuthenticated, (req, res) => {
    res.send(db.getElements('users'));
});

app.get('/user/:userId', isAuthenticated, (req, res) => {
    const userId = req.params.userId;

    const foundUser = db.getElement('users', { userId });

    if (foundUser) {
        res.send(foundUser);
    } else {
        res.status(404).end();
    }
});

app.get('/me/message', isAuthenticated, (req, res) => {
    const token = req.header('authorization');

    const foundToken = db.getElement('tokens', { token });
    const foundUser = db.getElement('users', { userId: foundToken.userId });

    const messages = [];

    const sentMessages = db.getFilteredElements('messages', {
        destiny: foundUser.userId,
    });

    if (sentMessages) {
        sentMessages.forEach(msg => messages.push(msg))
    }

    const receivedMessages = db.getFilteredElements('messages', {
        sender: foundUser.userId,
    });

    if (receivedMessages) {
        receivedMessages.forEach(msg => messages.push(msg))
    }

    res.send(messages);
});

io.on('connection', (socket) => {
    console.log(socket.id);

    socket.on('auth', (token) => {
        if (token) {
            const foundToken = db.getElement('tokens', { token });

            if (foundToken) {
                setListeners(socket, foundToken.userId);
            } else {
                socket.disconnect();
            }
        }
    });
});

function setListeners(socket, userId) {

    socket.join(userId, (err) => console.log(err));

    socket.on('new_message', (msg) => {
        const parsedMsg = JSON.parse(msg);
        parsedMsg.sender = userId;
        parsedMsg.id = shortid();

        db.setElement('messages', parsedMsg);

        io.to(parsedMsg.destiny).emit('new_message', parsedMsg);
        io.to(parsedMsg.sender).emit('new_message', parsedMsg);
    });

    socket.on('typing', (data) => {
        io.emit(data);
    });

    socket.on('stop_typing', (data) => {
        io.emit(data);
    });
}

function isAuthenticated(req, res, next) {
    const token = req.header('authorization');

    if (!token) {
        next('No token given!');
        return;
    }

    const foundToken = db.getElement('tokens', { token });

    if (foundToken) {
        next();
        return;
    }

    next('Invalid token');
}

server.listen(port, () => console.log(`Listening on port ${port}!`))