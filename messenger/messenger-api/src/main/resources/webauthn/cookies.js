function getCookie(name) {
    var cookies = document.cookie;
    var parts = cookies.split(name + "=");
    var cookieValue = '';
    if (parts.length == 2) {
        cookieValue = parts.pop().split(";").shift();
    }
    return cookieValue;
}
