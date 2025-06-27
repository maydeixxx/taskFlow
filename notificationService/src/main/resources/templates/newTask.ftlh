<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>New Task Assigned</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
        }
        .container {
            max-width: 600px;
            margin: 20px auto;
            background-color: #ffffff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        .header {
            background-color: #28a745;
            color: #ffffff;
            padding: 10px;
            text-align: center;
            border-radius: 8px 8px 0 0;
        }
        .content {
            padding: 20px;
            line-height: 1.6;
        }
        .button {
            display: inline-block;
            padding: 10px 20px;
            background-color: #28a745;
            color: #ffffff;
            text-decoration: none;
            border-radius: 5px;
            margin-top: 20px;
        }
        .footer {
            text-align: center;
            font-size: 12px;
            color: #777777;
            padding-top: 20px;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <h1>New Task Assigned, ${name}!</h1>
    </div>
    <div class="content">
        <p>You have been assigned a new task: <strong>${title}</strong>.</p>
        <p>Task ID: ${taskId}</p>
        <p>Check the task details and start working on it!</p>
        <a href="https://taskflow.example.com/tasks/${taskId}" class="button">View Task</a>
    </div>
    <div class="footer">
        <p>&copy; 2025 TaskFlow. All rights reserved.</p>
    </div>
</div>
</body>
</html>