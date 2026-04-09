import React from 'react'
import { useDispatch, useSelector } from 'react-redux'
import type { RootState } from '../store'
import { openAuth } from '../store/slices/uiSlice'

const AuthModal = () => {

    const { isAuthOpen, authMode } = useSelector((state: RootState) => state.ui);
    const guestID = localStorage.getItem('guestId');

    const dispatch = useDispatch();

    return (
        <div className='fixed inset-0 bg-black/50 backdrop-blur-sm z-50'>
            <div className='absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-white dark:bg-black p-8 rounded-lg shadow-lg flex flex-col items-center justify-center w-[50%]'>
                <div className="flex flex-row w-full">
                    <button className={`text-lime-500 m-2 p-2 rounded-lg hover:bg-lime-500 hover:text-black w-[50%] dark:text-lime-500 overflow-hidden ${authMode === 'login' ? 'bg-lime-500 dark:text-black' : ''}`} onClick={() => dispatch(openAuth('login'))}>Login</button>
                    <button className={`text-lime-500 m-2 p-2 rounded-lg hover:bg-lime-500 hover:text-black w-[50%] dark:text-lime-500 overflow-hidden ${authMode === 'register' ? 'bg-lime-500 dark:text-black' : ''}`} onClick={() => dispatch(openAuth('register'))}>Register</button>
                </div>

                <div className='flex flex-col items-center justify-between w-full'>
                    <h2 className='text-2xl text-lime-400 font-bold mb-4'>Authentication</h2>
                    <button className='text-lime-500 dark:text-lime-500'>Close</button>
                    <p className='text-lime-500 dark:text-lime-500'>Guest ID: {guestID}</p>
                </div>
            </div>
        </div>
    )
}

export default AuthModal