var app = require('express')();
var http = require('http').createServer(app);
var io = require('socket.io')(http);

app.get('/', function(req, res){
    res.sendFile(__dirname + '/index.html');
});

http.listen(3000, function(){
    console.log('listening on *:3000');
});


// Навешиваем обработчик на подключение нового клиента
io.sockets.on('connection', function (socket) {

    /**
     * Создаём нового юзера после его коннекта и засовываем в комнату.
     */
    socket.on('room-new-user', (room, user) => {
        console.log(['room-new-user', room, user]);
        socket.join(room);
        socket.to(room).broadcast.emit('user-connected', {user: user, room: room});
    });

    /**
     */
    socket.on('room-send-message', (room, user, message) => {
        console.log(['room-send-message', room, user, message]);
        socket.to(room).broadcast.emit('room-send-message', {user: user, message: message, room: room});
    });

    /**
     * Подтверждение получения сообщения
     */
    socket.on('room-confirm', (room, user) => {
        console.log(['room-confirm', room, user]);
        socket.to(room).broadcast.emit('room-confirm', {user: user, room: room});
    });

    /**
     * Подтверждение получения сообщения
     */
    socket.on('room-send-message1', (room, user) => {
        console.log(['room-send-message1', room, user]);
        socket.to(room).broadcast.emit('room-send-message1', {user: user, room: room});
    });

    // При отключении клиента - уведомляем остальных
    socket.on('disconnect', function() {
        //var time = (new Date).toLocaleTimeString();
        //io.sockets.json.send({'event': 'userSplit', 'name': ID, 'time': time});
    });
});