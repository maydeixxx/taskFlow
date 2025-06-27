<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Task Reminder</title>
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
            background-color: #dc3545;
            color: #ffffff;
            padding: 10px;
            text-align: center;
            border-radius: 8px 8-px 0 0;
        }
        .content {
            padding: 20px;
            line-height: 1.6;
        }
        .button {
            display: inline-block;
            padding: 10px 20px;
            background-color: #dc3545;
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
        <h1>Task Reminder, ${name}!</h1>
    </div>
    <div class="content">
        <p>You have a task due soon: <strong>${title}</strong>.</p>
        <p>Please complete it within the next 12 hours.</p>
        <a href="https://taskflow.example.com/tasks/${taskId}" class="button">View Task</a>
    </div>
    <div class="footer">
        <p>&copy; 2025 TaskFlow. All rights reserved.</p>
    </div>
</div>
</body>
</html>