import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { type RootState } from '../store';
import { closeAuth, openAuth } from '../store/slices/uiSlice';
import { setAuth } from '../store/slices/authSlice';
import api from '../api/api';

const AuthModal = () => {
    const dispatch = useDispatch();
    const { isAuthOpen, authMode } = useSelector((state: RootState) => state.ui);

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');

    if (!isAuthOpen) return null;

    const handleAuth = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');

        const guestId = localStorage.getItem('guestId');
        const endpoint = authMode === 'login' ? '/auth/login' : '/auth/register';

        try {
            const res = await api.post(endpoint, { username, password, guestId });

            if (authMode === 'login') {
                dispatch(setAuth({
                    token: res.data.token,
                    user: res.data.username
                }));
                dispatch(closeAuth());
            } else {
                dispatch(openAuth('login'));
                setError('Account created. Please login.');
            }
        } catch (err: any) {
            setError(err.response?.data?.message || 'Authentication failed.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="fixed inset-0 z-100 flex items-center justify-center bg-black/25 backdrop-blur-2xl transition-all duration-300" style={{ backdropFilter: 'blur(10px)', WebkitBackdropFilter: 'blur(10px)' }} >
            <div className="relative w-full max-w-md scale-100 transform overflow-hidden rounded-2xl p-8 transition-all duration-300
                    bg-white border border-zinc-200 shadow-[0_20px_50px_rgba(0,0,0,0.1)] 
                    dark:bg-black dark:border-white/10 dark:shadow-[0_20px_50px_rgba(0,0,0,0.5)] dark:shadow-lime-500/5">

                <button
                    onClick={() => dispatch(closeAuth())}
                    className="absolute right-4 top-4 text-zinc-500 hover:text-white transition-colors"
                >
                    X
                </button>

                <div className="mb-8 flex justify-center space-x-4 border-b border-white/5 pb-4">
                    <button
                        onClick={() => dispatch(openAuth('login'))}
                        className={`text-lg font-bold transition-all
                            ${authMode === 'login'
                                ? 'text-lime-600 dark:text-lime-500 underline underline-offset-8'
                                : 'text-zinc-400 dark:text-zinc-600 hover:text-zinc-900 dark:hover:text-zinc-300'}`}
                    >
                        Login
                    </button>
                    <button
                        onClick={() => dispatch(openAuth('register'))}
                        className={`text-lg font-bold transition-all
                            ${authMode === 'register'
                                ? 'text-lime-600 dark:text-lime-500 underline underline-offset-8'
                                : 'text-zinc-400 dark:text-zinc-600 hover:text-zinc-900 dark:hover:text-zinc-300'}`}
                    >
                        Register
                    </button>
                </div>

                <form onSubmit={handleAuth} className="space-y-6">
                    <div>
                        <label className="mb-2 block text-sm font-medium text-zinc-400">Username</label>
                        <input
                            type="text"
                            required
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            className="w-full rounded-xl px-4 py-3 outline-none transition-all focus:ring-4 focus:ring-lime-500/20
                                    bg-zinc-100 border border-zinc-200 text-zinc-900 placeholder-zinc-400 focus:border-zinc-900
                                    dark:bg-white/5 dark:border-white/10 dark:text-white dark:focus:border-lime-500"
                            placeholder="Account handle"
                        />
                    </div>

                    <div>
                        <label className="mb-2 block text-sm font-medium text-zinc-400">Password</label>
                        <input
                            type="password"
                            required
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            className="w-full rounded-xl px-4 py-3 outline-none transition-all focus:ring-4 focus:ring-lime-500/20
                                    bg-zinc-100 border border-zinc-200 text-zinc-900 placeholder-zinc-400 focus:border-zinc-900
                                    dark:bg-white/5 dark:border-white/10 dark:text-white dark:focus:border-lime-500"
                            placeholder="••••••••"
                        />
                    </div>

                    {error && (
                        <p className={`text-center text-sm ${error.includes('created') || error.includes('success') ? 'text-lime-500' : 'text-red-400'}`}>
                            {error}
                        </p>
                    )}

                    <button
                        type="submit"
                        disabled={isLoading}
                        className="group relative flex w-full items-center justify-center overflow-hidden rounded-xl bg-lime-500 px-8 py-4 font-bold text-black transition-all hover:scale-[1.02] active:scale-[0.98] disabled:opacity-50"
                    >
                        {isLoading ? (
                            <div className="h-5 w-5 animate-spin rounded-full border-2 border-black border-t-transparent" />
                        ) : (
                            authMode === 'login' ? 'Proceed to Vault' : 'Initialize Account'
                        )}
                        <div className="absolute inset-0 -translate-x-full bg-white/20 transition-transform group-hover:translate-x-0" />
                    </button>
                </form>

                <p className="mt-6 text-center text-xs text-zinc-500">
                    By accessing this archive, you acknowledge the standard security protocols.
                </p>
            </div>
        </div>
    );
};

export default AuthModal;


