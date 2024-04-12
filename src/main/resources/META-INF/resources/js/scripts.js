var socket = null;

document.addEventListener("DOMContentLoaded", function() {
    initializeWebSocket(); // Establish WebSocket connection
    initializeChat();      // Set up chat input handlers
    fetchData();           // Fetch dashboard data
});

function initializeWebSocket() {
    socket = new WebSocket('ws://' + window.location.hostname + ':8080/api/chat');
    socket.onopen = function(event) {
        console.log("Connection established!");
        document.getElementById('send').disabled = false; // Enable send button once the WS connection is open
    };
    socket.onmessage = function(event) {
        console.log("Received reply: " + event.data);
        const chat = document.getElementById('chat');
        chat.value += "Bot: " + event.data + "\n";  // Display the server's reply
        chat.scrollTop = chat.scrollHeight; // Auto scroll to the latest message
    };
    socket.onerror = function(event) {
        console.error("WebSocket error:", event);
    };
    socket.onclose = function(event) {
        console.log("WebSocket closed.");
        document.getElementById('send').disabled = true; // Disable send button if the WS connection is closed
    };
}

function initializeChat() {
    const input = document.getElementById('msg');
    const sendButton = document.getElementById('send');

    input.addEventListener('input', function() {
        sendButton.disabled = !this.value.trim(); // Enable button only if there is text
    });

    input.addEventListener('keypress', function(e) {
        if (e.key === 'Enter' && !sendButton.disabled) {
            e.preventDefault(); // Prevent default to not submit form if any
            sendMessage();
        }
    });

    sendButton.addEventListener('click', sendMessage);
}

function sendMessage() {
    const input = document.getElementById('msg');
    const chat = document.getElementById('chat');
    const message = input.value.trim();

    if (!message) return; // Don't send an empty message

    // Display the user's message in the chatbox
    chat.value += "You: " + message + "\n";
    chat.scrollTop = chat.scrollHeight;

    // Send the message to the server via WebSocket
    if (socket.readyState === WebSocket.OPEN) {
        socket.send(message);
    } else {
        console.error('WebSocket is not open. Message not sent.');
        chat.value += "Bot: Connection not open.\n";
    }

    input.value = ''; // Clear input after sending
    document.getElementById('send').disabled = true; // Disable send button again
}

function fetchData() {
    fetch('http://localhost:8080/api/dashboard', {
        method: 'GET',
        headers: {
            'Accept': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => renderData(data))
    .catch(error => console.error('Error fetching data:', error));
}

function renderData(data) {
    const analysisContainer = document.getElementById('analysis-container');
    const feedbacksContainer = document.getElementById('feedbacks-container');

    data.analysis.forEach(item => {
        const analysisDiv = document.createElement('div');
        analysisDiv.className = 'analysis';
        analysisDiv.innerHTML = `<strong>${item.name}</strong>: ` + JSON.stringify(item.values);
        analysisContainer.appendChild(analysisDiv);
    });

    data.feedbacks.forEach(feedback => {
        const feedbackDiv = document.createElement('div');
        feedbackDiv.className = 'feedback';
        feedbackDiv.innerHTML = `<strong>${feedback.category}</strong>: ${feedback.feedback} <br>
                                 <strong>Impact</strong>: ${feedback.impact} <br>
                                 <strong>Severity</strong>: ${feedback.severity} <br>
                                 <strong>Urgency</strong>: ${feedback.urgency} <br>
                                 <strong>Tags</strong>: ${feedback.tags.join(', ')}`;
        feedbacksContainer.appendChild(feedbackDiv);
    });
}