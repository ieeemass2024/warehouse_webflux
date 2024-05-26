import axios from 'axios';
import router from './router';
import Swal from 'sweetalert2';

axios.interceptors.response.use(response => response, error => {
    if (error.response && error.response.status === 401) {
        localStorage.removeItem('jwtToken');
        Swal.fire({
            title: 'Session Expired!',
            text: 'Your session has expired, you need to login again.',
            icon: 'warning',
            confirmButtonText: 'Login'
        }).then(result => {
            if (result.isConfirmed) {
                router.push({ name: 'Login' });
            }
        });
    }
    return Promise.reject(error);
});

export default axios;
