import axios from 'axios';
import { useAuthStore } from '@/stores/auth';

const api = axios.create({
  baseURL: '/api/setting',
  headers: { 'Content-Type': 'application/json' }
});

export default {

  async updateProfile(userInfo) {

    let headers = { 'Content-Type': 'multipart/form-data' };

    const formData = new FormData();

    formData.append('userNo', userInfo.userNo);
    formData.append('address', userInfo. address);
    formData.append('postCode', userInfo.postCode);
    formData.append('countryCode', userInfo.countryCode);
    
    if (userInfo.profilePic) {
      formData.append('profilePicFile', userInfo.profilePic);
    }
    console.log('---------------야');
    const { data } = await api.post('/', formData, { headers });
    console.log('updateProfile: ', data);
    
    
    const auth = useAuthStore();
    auth.updateProfileState(data);
    return data;
  },

  async changePassword(formData) {
    console.log('formData: ', formData);
    const { data } = await api.patch('/change-password', formData);
    console.log('AUTH PUT: ', data);
    return data;
  },

  async delete(userNo) {
    console.log('userNo: ', userNo);
    const { data } = await api.delete(`/bye/${userNo}`);
    console.log('delete: ', data);
    window.location.href = '/';
  },

}