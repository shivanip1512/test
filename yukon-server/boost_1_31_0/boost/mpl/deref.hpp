//-----------------------------------------------------------------------------
// boost mpl/deref.hpp header file
// See http://www.boost.org for updates, documentation, and revision history.
//-----------------------------------------------------------------------------
//
// Copyright (c) 2002-03
// Aleksey Gurtovoy
//
// Permission to use, copy, modify, distribute and sell this software
// and its documentation for any purpose is hereby granted without fee, 
// provided that the above copyright notice appears in all copies and 
// that both the copyright notice and this permission notice appear in 
// supporting documentation. No representations are made about the 
// suitability of this software for any purpose. It is provided "as is" 
// without express or implied warranty.

#ifndef BOOST_MPL_DEREF_HPP_INCLUDED
#define BOOST_MPL_DEREF_HPP_INCLUDED

#include "boost/mpl/aux_/void_spec.hpp"
#include "boost/mpl/aux_/lambda_support.hpp"
#include "boost/mpl/aux_/config/eti.hpp"

namespace boost {
namespace mpl {

template<
      typename BOOST_MPL_AUX_VOID_SPEC_PARAM(Iterator)
    >
struct deref
{
    typedef typename Iterator::type type;
    BOOST_MPL_AUX_LAMBDA_SUPPORT(1,deref,(Iterator))
};

#if defined(BOOST_MPL_MSVC_ETI_BUG)
template<> struct deref<int>
{
    typedef int type;
};
#endif

BOOST_MPL_AUX_VOID_SPEC(1, deref)

} // namespace mpl
} // namespace boost

#endif // BOOST_MPL_DEREF_HPP_INCLUDED
����l?�>��k�P	Xk��ؤi� �Zm�E�'�����/r�g��Mq4B!�T]��s�<-~���63��n�x�Q�x��U��9���
��_�V���ƍq��nT��`מٴ��i���B�����=���^�%B9[
{
�zF��f���8�;}U����� �A��2Mm��jC�Z��xLێ?���ݡ����8(
ͼ�
��oX�y R7L3�S x�DuHO�x�ȎG�HkX��3lU�!K���p+��(�����;�a���wQ�C͂+��#��62��a[{#"$�4�"�XG9��C����|�",U��k�M��.�HOWX�N�d�Uw��>6%��x��_���u'T�ǟߴ��.�K�"�_����f@��4I�B��ha��h�-�в��|�
g*��e��Q?����D�6�:�W�t|��N���B���Bw�Ghxo���n��ջ#Ε�tY���k��&����@:ۼz׹e���d ��IMVjV�	��(Z�F��Ω��9_�k �V5T�y�Y���4���{͕�ԌnW޹H���� ݸ��{��ʿЀ�\�U��/v�Yۄ/�k~>o~��G��[3�MUU:J�[X��U�qB�e+w��E���ֿ)+Xg�]���j@O���_.����O&�o�PT�v:����_�N�IX%�#A���Ϲ�Ka@���