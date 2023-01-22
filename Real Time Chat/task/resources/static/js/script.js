
var stompClient = null;

function addMessage(){
    const container = document.getElementById("messages")
    const text = document.getElementById("input-msg").value
    sendContent();
    document.getElementById("input-msg").value="";
};
function sendUsername(){
    connect();
    $("#input-msg")[0].style.visibility='';
    $("#chat-with")[0].style.visibility='';
    $("#send-msg-btn")[0].style.visibility='';
    $("#public-chat-btn")[0].style.visibility='';
    $("#send-username-btn")[0].style.visibility='hidden';
    $("#input-username")[0].style.visibility='hidden';
    const username = $('#input-username')[0].value;
    document.title = username;
    fetch("/allMessages").then(function(res){
        return res.json()
    }).then(function(data) {
                  data.forEach(obj => {
                            content="";
                            sender="";
                            date="";
                            Object.entries(obj).forEach(([key, value]) => {
                                if(key=='content'){
                                    content = value;
                                }
                                else if(key=='name')
                                    sender=value;
                                else if(key=='date')
                                    date=value;
                          });
                          showMessage(content,sender,date)

                      });
              });
}
function sendContent() {
    const date = new Date();
    recipient = $("#chat-with")[0].innerHTML;
    if(recipient=="Public chat")
        recipient="";
    stompClient.send("/app/send", {}, JSON.stringify({'name': $("#input-username")[0].value, 'content': $("#input-msg")[0].value, 'date':date.toString('D/MM/YYYY'), 'recipient':recipient}));
}
function showMessage(message,sender,date) {
    $("#messages").append("<div class='message-container'>"+
                            "<div class='sender'>"+sender+"</div>"+
                            "<div class='date'>"+date+"</div>"+
                            "<div class='message'>"+ message + "</div></div>");
}
function connect() {
    username = $('#input-username')[0].value;

    var socket = new SockJS('/websocket');

    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.send("/app/chat.newUser", {}, JSON.stringify({
                        name : username,
                        content : 'newUser'
                    }))
        stompClient.subscribe('/topic/messages', function (message) {
            if($("#chat-with")[0].innerHTML=='Public chat'&&JSON.parse(message.body).recipient=="")
                showMessage(JSON.parse(message.body).content,JSON.parse(message.body).name,JSON.parse(message.body).date);
            else if ($("#chat-with")[0].innerHTML!='Public chat'&&(JSON.parse(message.body).recipient==username||JSON.parse(message.body).recipient==$("#chat-with")[0].innerHTML))
                 showMessage(JSON.parse(message.body).content,JSON.parse(message.body).name,JSON.parse(message.body).date);
             else if($("#chat-with")[0].innerHTML!=JSON.parse(message.body).name&&
                    JSON.parse(message.body).recipient==username){

                    users = $('.user');
                    users.each(function() {
                        if($(this).text().includes(JSON.parse(message.body).name)){
                            counter = $(this).find('.new-message-counter')[0].innerText;
                            if(counter =="")
                                counter = 0;
                            else
                                counter = parseInt(counter);
                            $(this).find('.new-message-counter')[0].innerText=counter+1;
                            $(this).prependTo("#users")
                        }

                    })
                }
        });

        stompClient.subscribe('/topic/users', function (message) {
              userProcession(JSON.parse(message.body).name,JSON.parse(message.body).content);
          });
        fetch("/allUsers").then(function(res){
                        return res.json()
                    }).then(function(data) {

                        data.forEach(element => {if(username!=element) addUser(element)});
                    });

    });
}
function userProcession(username, status){
    name = $('#input-username')[0].value;
    if(username!=name){
        if(status=="Leave"){
            users = $(".user")
            users.each(function() {
                if($(this).text()==username)
                    $(this).remove();
            })
        }
        else if(status=='newUser'){
            addUser(username);
        }
    }
}
function addUser(name){
    $('#users').append("<div class='user user-container'>"+name+"<div class='new-message-counter'></div></div>")
    users = $('.user')
    users.each(function() {
        if($(this).text()==name)
            $(this).click(function(){
                $("#chat-with")[0].innerHTML=this.textContent;
                $("#messages")[0].innerHTML = '';
                fetch("/allMessages/"+this.textContent+"/"+document.title).then(function(res){
                                return res.json()
                            }).then(function(data) {
                                          data.forEach(obj => {
                                                    content="";
                                                    sender="";
                                                    date="";
                                                    Object.entries(obj).forEach(([key, value]) => {
                                                        if(key=='content'){
                                                            content = value;
                                                        }
                                                        else if(key=='name')
                                                            sender=value;
                                                        else if(key=='date')
                                                            date=value;
                                                  });
                                                  showMessage(content,sender,date)

                                              });
                                      });
            })

    })
}
$(function () {
    $("#input-msg")[0].style.visibility='hidden';
    $("#send-msg-btn")[0].style.visibility='hidden';
    $("#public-chat-btn")[0].style.visibility='hidden';
    $("#public-chat-btn").click(function(){
        $("#chat-with")[0].innerHTML="Public chat";
        $("#messages")[0].innerHTML = '';
        fetch("/allMessages").then(function(res){
                return res.json()
            }).then(function(data) {
                          data.forEach(obj => {
                                    content="";
                                    sender="";
                                    date="";
                                    Object.entries(obj).forEach(([key, value]) => {
                                        if(key=='content'){
                                            content = value;
                                        }
                                        else if(key=='name')
                                            sender=value;
                                        else if(key=='date')
                                            date=value;
                                  });
                                  showMessage(content,sender,date)

                              });
                      });
    });
    $("#chat-with")[0].style.visibility='hidden';
    $( "#send-msg-btn" ).click(function()
    {
        addMessage();
    });
    $("#send-username-btn").click(function(){
        sendUsername();
    })
});

