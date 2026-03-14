<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Connexion - ERP Clinique</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #2c3e50;
        }

        .login-wrapper {
            display: flex;
            align-items: stretch;
            max-width: 900px;
            width: 100%;
            background: rgba(255,255,255,0.08);
            border-radius: 16px;
            box-shadow: 0 18px 45px rgba(0,0,0,0.35);
            overflow: hidden;
        }

        .login-hero {
            flex: 1;
            padding: 30px;
            color: #fdfefe;
            display: flex;
            flex-direction: column;
            justify-content: center;
            background: linear-gradient(160deg, rgba(255,255,255,0.18), rgba(0,0,0,0.05));
        }

        .login-hero h1 {
            font-size: 28px;
            margin-bottom: 10px;
        }

        .login-hero p {
            margin-top: 8px;
            font-size: 14px;
            opacity: 0.9;
        }

        .login-hero ul {
            margin-top: 18px;
            font-size: 13px;
            list-style: none;
            padding-left: 0;
        }

        .login-hero li {
            margin-bottom: 6px;
        }

        .login-container {
            width: 380px;
            background: #fdfefe;
            padding: 35px 30px;
        }

        .login-title {
            text-align: center;
            margin-bottom: 25px;
        }

        .login-title span {
            display: block;
            font-size: 13px;
            color: #7f8c8d;
            margin-top: 4px;
        }

        .form-group {
            margin-bottom: 15px;
        }

        label {
            display: block;
            margin-bottom: 6px;
            color: #2c3e50;
            font-size: 13px;
            font-weight: 600;
        }

        input {
            width: 100%;
            padding: 10px 12px;
            border: 1px solid #d5d8dc;
            border-radius: 6px;
            font-size: 14px;
        }

        input:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 2px rgba(102,126,234,0.2);
        }

        .login-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 5px;
            margin-bottom: 15px;
            font-size: 12px;
            color: #7f8c8d;
        }

        .remember {
            display: flex;
            align-items: center;
            gap: 4px;
        }

        .remember input {
            width: auto;
        }

        button {
            width: 100%;
            padding: 11px;
            background: #667eea;
            color: white;
            border: none;
            border-radius: 6px;
            font-size: 15px;
            cursor: pointer;
            font-weight: 600;
        }

        button:hover {
            background: #5a67d8;
        }

        .error {
            background: #fed7d7;
            color: #c53030;
            padding: 10px 12px;
            border-radius: 6px;
            margin-bottom: 15px;
            font-size: 13px;
        }

        .hint {
            margin-top: 12px;
            font-size: 12px;
            color: #7f8c8d;
        }

        .hint code {
            background: #f4f6f7;
            padding: 1px 4px;
            border-radius: 3px;
        }

        @media (max-width: 780px) {
            .login-wrapper {
                flex-direction: column;
                max-width: 420px;
            }

            .login-hero {
                display: none;
            }

            .login-container {
                width: 100%;
            }
        }
    </style>
</head>
<body>
<div class="login-wrapper">
    <div class="login-hero">
        <h1>🏥 ERP Clinique</h1>
        <p>Gérez vos patients, rendez-vous et consultations dans une interface simple et moderne.</p>
        <ul>
            <li>• Vue d’ensemble des patients et médecins</li>
            <li>• Suivi des rendez-vous et consultations</li>
            <li>• Espace dédié pour les médecins</li>
        </ul>
    </div>

    <div class="login-container">
        <div class="login-title">
            <strong>Connexion</strong>
            <span>Entrez vos identifiants pour accéder à la plateforme</span>
        </div>

        <c:if test="${not empty error}">
            <div class="error">${error}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post">
            <div class="form-group">
                <label>Login</label>
                <input type="text" name="login" required autofocus>
            </div>
            <div class="form-group">
                <label>Mot de passe</label>
                <input type="password" name="password" required>
            </div>

            <div class="login-footer">
                <label class="remember">
                    <input type="checkbox" name="rememberMe">
                    <span>Se souvenir de moi</span>
                </label>
                <span>Mot de passe oublié ?</span>
            </div>

            <button type="submit">Se connecter</button>
        </form>
    </div>
</div>
</body>
</html>