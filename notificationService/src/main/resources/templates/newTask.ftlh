<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>New Task Assigned</title>
    <style>
        body {
            font-family: 'Arial', sans-serif;
            background-color: #f0f2f5;
            margin: 0;
            padding: 0;
            color: #333333;
        }
        .container {
            max-width: 600px;
            margin: 40px auto;
            background-color: #ffffff;
            border: 2px solid #e0e0e0;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }
        .header {
            background-color: #007bff;
            color: #ffffff;
            text-align: center;
            padding: 20px;
            border-bottom: 2px solid #0056b3;
            border-radius: 8px 8px 0 0;
        }
        .header h1 {
            margin: 0;
            font-size: 28px;
            font-weight: 700;
        }
        .content {
            padding: 30px;
            text-align: center;
            border-top: 1px solid #e0e0e0;
        }
        .content p {
            font-size: 16px;
            line-height: 1.6;
            margin-bottom: 20px;
            color: #4a4a4a;
            font-weight: 500;
        }
        .button {
            display: inline-block;
            padding: 12px 25px;
            background-color: #007bff;
            color: #ffffff;
            text-decoration: none;
            border: 2px solid #0056b3;
            border-radius: 5px;
            font-size: 16px;
            font-weight: 600;
            transition: background-color 0.3s, color 0.3s;
        }
        .button:hover {
            background-color: #0056b3;
            color: #ffffff;
        }
        .footer {
            text-align: center;
            padding: 15px;
            font-size: 12px;
            color: #777777;
            border-top: 1px solid #e0e0e0;
            background-color: #f9f9f9;
        }
        @media only screen and (max-width: 600px) {
            .container {
                margin: 20px;
                width: calc(100% - 40px);
            }
            .header h1 {
                font-size: 24px;
            }
            .content p {
                font-size: 14px;
            }
            .button {
                padding: 10px 20px;
                font-size: 14px;
            }
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <h1>New Task for ${name}!</h1>
    </div>
    <div class="content">
        <p>A new task has been assigned to you: "${title}".</p>
        <p>Task ID: ${taskId}. Please review and start working on it.</p>
        <a href="https://taskflow.example.com/tasks/${taskId}" class="button">View Task</a>
    </div>
    <div class="footer">
        <p>© 2025 TaskFlow. All rights reserved.</p>
    </div>
</div>
</body>
</html>