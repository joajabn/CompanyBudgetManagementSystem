<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${action} User</title>
</head>
<body>
    <h2>${action} User</h2>

    <form action="${pageContext.request.contextPath}/users/save" method="post">
        <input type="hidden" name="username" value="${user.username}" />

        <div>
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" value="${user.username}" ${action == 'Edit' ? 'readonly' : ''} required />
        </div>
        <div>
            <label for="email">Email:</label>
            <input type="email" id="email" name="email" value="${user.email}" required />
        </div>
        <div>
            <label for="password">Password:</label>
            <input type="password" id="password" name="password" ${action == 'Edit' ? '' : 'required'} />
        </div>
        <div>
            <label for="role">Role:</label>
            <select id="role" name="role" required>
                <option value="ADMIN" ${user.role == 'ADMIN' ? 'selected' : ''}>Admin</option>
                <option value="USER" ${user.role == 'USER' ? 'selected' : ''}>User</option>
            </select>
        </div>

        <div>
            <button type="submit">${action} User</button>
        </div>

        <input type="hidden" name="action" value="${action}" />
    </form>

    <a href="${pageContext.request.contextPath}/users">Back to Users List</a>
</body>
</html>
