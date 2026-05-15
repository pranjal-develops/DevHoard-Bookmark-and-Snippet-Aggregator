const api = (typeof chrome !== "undefined") ? chrome : (typeof browser !== "undefined" ? browser : null);

const handshake = async () => {
    // 1. Get IDs and Tokens from the WEBSITE
    const webId = localStorage.getItem('guestId');
    const webToken = localStorage.getItem('token');

    if (!api || !api.storage) {
        console.error("DevHoard: Extension API not accessible in content script!");
        return;
    }

    const storage = await new Promise(r => api.storage.local.get(['guestId', 'token', 'logout_signal'], r));
    const extId = storage.guestId;
    const extToken = storage.token;

    // --- LOGOUT SIGNAL SYNC ---
    if (storage.logout_signal) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        // Clear the signal so we don't loop
        await new Promise(r => api.storage.local.remove(['logout_signal'], r));
        window.location.reload();
        return; // Stop here
    }

    // --- TOKEN SYNC ---
    if (webToken && webToken !== extToken) {
        await new Promise(r => api.storage.local.set({ token: webToken }, r));
    } else if (extToken && !webToken) {
        localStorage.setItem('token', extToken);
        window.location.reload();
    }

    // --- GUEST ID SYNC ---
    if (webId && webId !== extId) {
        if (extId && extId.startsWith('ext-')) {
            await fetch(`https://devhoard-api.onrender.com/api/bookmarks/migrate-guest?from=${extId}&to=${webId}`, { method: 'POST' });
        }
        await new Promise(r => api.storage.local.set({ guestId: webId }, r));
    } else if (extId && !webId) {
        localStorage.setItem('guestId', extId);
    }
};

handshake().catch(err => console.error("Handshake Failed:", err));
