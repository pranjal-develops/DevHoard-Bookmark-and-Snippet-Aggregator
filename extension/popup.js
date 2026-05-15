const api = (typeof chrome !== "undefined") ? chrome : (typeof browser !== "undefined" ? browser : null);

// --- UI Toggle Helper ---
const showSection = (id) => {
    document.getElementById('login-section').classList.add('hidden');
    document.getElementById('save-section').classList.add('hidden');
    document.getElementById(id).classList.remove('hidden');
};

// --- Initialization ---
const init = async () => {
    const storage = await new Promise(r => api.storage.local.get(['token', 'guestId'], r));

    if (!storage.guestId) {
        const newId = 'ext-' + Math.random().toString(36).substr(2, 9);
        await new Promise(r => api.storage.local.set({ guestId: newId }, r));
    }

    showSection('save-section');

    const loginHint = document.getElementById('login-hint');
    const logoutBtn = document.getElementById('logout-btn');

    if (!storage.token) {
        loginHint.classList.remove('hidden');
        logoutBtn.classList.add('hidden'); // Hide logout if guest
    } else {
        loginHint.classList.add('hidden');
        logoutBtn.classList.remove('hidden'); // Show logout if user
    }
};

document.getElementById('logout-btn').addEventListener('click', async () => {
    // 1. Send a "Logout Signal" that the content script can see
    await new Promise(r => api.storage.local.set({ logout_signal: true }, r));

    // 2. Remove the token from the extension
    await new Promise(r => api.storage.local.remove(['token'], r));

    window.location.reload();
});



document.getElementById('go-to-login').addEventListener('click', (e) => {
    e.preventDefault();
    showSection('login-section');
});

// --- Login Logic ---
document.getElementById('login-btn').addEventListener('click', async () => {
    const user = document.getElementById('username').value;
    const pass = document.getElementById('password').value;
    const error = document.getElementById('login-error');

    try {
        const response = await fetch('https://devhoard-api.onrender.com/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username: user, password: pass })
        });

        if (response.ok) {
            const data = await response.json();
            await new Promise(r => api.storage.local.set({ token: data.token }, r));
            window.location.reload(); // Refresh to show the save section
        } else {
            error.textContent = "Invalid username or password";
            error.classList.remove('hidden');
        }
    } catch (e) {
        error.textContent = "Connection failed";
        error.classList.remove('hidden');
    }
});

// --- Archival Logic ---
document.getElementById('save-btn').addEventListener('click', async () => {
    const status = document.getElementById('status');
    const tagsInput = document.getElementById('tags').value;
    status.classList.remove('hidden');
    status.textContent = "🛰️ Archiving...";

    try {
        const tab = await new Promise(r => api.tabs.query({ active: true, currentWindow: true }, t => r(t[0])));
        const storage = await new Promise(r => api.storage.local.get(['token', 'guestId'], r));

        const response = await fetch('https://devhoard-api.onrender.com/api/bookmarks', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${storage.token}`
            },
            body: JSON.stringify({
                url: tab.url,
                categories: tagsInput.split(',').map(s => s.trim()).filter(s => s !== ""),
                guestId: storage.token ? null : storage.guestId
            })
        });

        if (response.ok) {
            status.textContent = "✅ Saved!";
            setTimeout(() => window.close(), 1500);
        } else {
            status.textContent = "❌ Error: " + response.status;
        }
    } catch (error) {
        status.textContent = "❌ Connection Error";
    }
});

init();
