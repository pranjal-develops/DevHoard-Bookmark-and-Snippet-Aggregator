import React, { useState } from 'react'; // React primitives for local state and event handling
import { useDispatch, useSelector } from 'react-redux'; // Redux hooks for state access and action dispatching
import { type RootState } from '../store'; // Global state type for selector orchestration
import { closeAuth, openAuth } from '../store/slices/uiSlice'; // UI controls for modal visibility
import { setAuth } from '../store/slices/authSlice'; // Authentication action for session persistence
import api from '../api/api'; // Centralized Axios instance for authenticated HTTP requests

/**
 * Authentication Modal Component.
 * Facilitates both 'Login' and 'Registration' workflows within a unified overlay.
 * Orchestrates identity-token retrieval and optional guest identity consolidation.
 */
const AuthModal = () => {
    const dispatch = useDispatch();

    // UI state extraction: managing modal visibility and the active sub-mode (login/register)
    const { isAuthOpen, authMode } = useSelector((state: RootState) => state.ui);

    // Local form state for credential inputs and feedback loops
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');

    /* Lifecycle Check: Early return if the modal state is inactive */
    if (!isAuthOpen) return null;

    /**
     * Primary Authentication Handler.
     * Executes the network request for either login or registration based on current 'authMode'.
     */
    const handleAuth = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');

        // Retrieval of current local identity context for backend consolidation
        const guestId = localStorage.getItem('guestId');

        // Resolution of the target endpoint based on the active modal tab
        const endpoint = authMode === 'login' ? '/auth/login' : '/auth/register';

        try {
            /**
             * Backend Execution:
             * Passing credentials alongside the 'guestId'. 
             * The 'guestId' acts as a consolidation signal, informing the backend to migrate 
             * orphaned bookmarks to the new/existing authenticated account.
             */
            const res = await api.post(endpoint, {
                username,
                password,
                guestId
            });

            if (authMode === 'login') {
                // Success Path (Login): Hydrating the global auth slice and terminating the modal lifecycle
                dispatch(setAuth({
                    token: res.data.token,
                    user: res.data.username
                }));
                dispatch(closeAuth());
            } else {
                /** 
                 * Success Path (Registration): 
                 * Redirecting the user to the Login view to complete the JWT handshake.
                 */
                dispatch(openAuth('login'));
                setError('Account successfully provisioned. Please enter your credentials to proceed.');
            }
        } catch (err: any) {
            // Failure Path: Abstracting raw network errors into user-friendly diagnostic messages
            setError(err.response?.data?.message || 'Authentication lifecycle failed. Please verify your credentials.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        /* Multi-layered backdrop with blur-effect for a premium interface depth */
        <div className="fixed inset-0 z-100 flex items-center justify-center bg-black/25 backdrop-blur-2xl transition-all duration-300" style={{ backdropFilter: 'blur(10px)', WebkitBackdropFilter: 'blur(10px)' }} >

            <div className="relative w-full max-w-md scale-100 transform overflow-hidden rounded-2xl p-8 transition-all duration-300
                    bg-white border border-zinc-200 shadow-[0_20px_50px_rgba(0,0,0,0.1)] 
                    dark:bg-black dark:border-white/10 dark:shadow-[0_20px_50px_rgba(0,0,0,0.5)] dark:shadow-lime-500/5">

                {/* /**
                 * Legacy UI Analysis:
                 * Commented code below indicates a planned transition to 'Heroicons' for a cleaner visual language.
                 * // import { XMarkIcon } from '@heroicons/react/24/outline';
                 * 
                 * Current Implementation:
                 * Utilizing a standard 'X' character as a lightweight fallback until dependency resolution is finalized.
                 */}
                <button
                    onClick={() => dispatch(closeAuth())}
                    className="absolute right-4 top-4 text-zinc-500 hover:text-white transition-colors"
                >
                    X   {/* <XMarkIcon className="h-6 w-6" /> <span className="text-2xl">X</span> */}
                </button>

                {/* Navigation Header: Orchestrates switching between Login and Registration contexts */}
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
                    {/* Credential Ingestion Points */}
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

                    {/* Feedback Layer: Displays errors or success notifications directly within the form flow */}
                    {error && (
                        <p className={`text-center text-sm ${error.includes('Success') || error.includes('created') || error.includes('successfully') ? 'text-lime-500' : 'text-red-400'}`}>
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

